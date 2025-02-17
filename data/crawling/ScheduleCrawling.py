import mysql.connector
import yaml
import requests
from bs4 import BeautifulSoup
import json
import re

def load_db_config(file_path):
    try:
        with open(file_path, "r", encoding="utf-8") as file:
            config = yaml.safe_load(file)
            if not config or "database" not in config:
                raise ValueError("YAML 파일에서 'database' 키를 찾을 수 없습니다.")
            return config.get("database")
    except FileNotFoundError:
        print(f"DB 설정 파일을 찾을 수 없습니다: {file_path}")
        return None
    except yaml.YAMLError as e:
        print(f"YAML 파일 파싱 오류: {e}")
        return None
    except ValueError as e:
        print(f"DB 설정 오류: {e}")
        return None

def connect_to_db(config):
    try:
        return mysql.connector.connect(
            host=config["host"],
            user=config["user"],
            password=config["password"],
            database=config["name"]
        )
    except mysql.connector.Error as e:
        print(f"DB 연결 오류: {e}")
        return None

def clean_text(text):
    return re.sub(r"\\r|\\n|\\t|\\s+", " ", text).strip()

def crawl_schedule_data(soup):
    try:
        schedule = []
        day_sections = soup.select(".temple-description h4.bullet")

        for day_title_element in day_sections:
            day_title = day_title_element.text.strip() if day_title_element else "null"
            table_element = day_title_element.find_next("table") if day_title_element else None
            if not table_element:
                continue

            day_schedule = {
                "day_title": day_title,
                "activities": []
            }

            rows = table_element.select("tbody tr")
            for row in rows:
                cells = row.select("td")
                if len(cells) >= 2:
                    time_slot = clean_text(cells[0].text)
                    activity = clean_text(cells[1].text)
                    day_schedule["activities"].append({"time": time_slot, "activity": activity})

            schedule.append(day_schedule)

        return json.dumps(schedule, ensure_ascii=False)

    except Exception as e:
        print(f"일정 크롤링 실패: {e}")
        return json.dumps([], ensure_ascii=False)

def fetch_templestay_urls(connection):
    try:
        cursor = connection.cursor()
        query = "SELECT id, templestay_url FROM templestay WHERE schedule IS NULL OR schedule = '' LIMIT 10"
        cursor.execute(query)
        urls = [(row[0], row[1]) for row in cursor.fetchall()]
        cursor.close()
        return urls
    except mysql.connector.Error as err:
        print(f"URL 가져오기 오류: {err}")
        return []

def update_schedule_in_db(connection, templestay_id, schedule):
    cursor = None
    try:
        cursor = connection.cursor()
        update_query = """
            UPDATE templestay 
            SET schedule = %s 
            WHERE id = %s
        """
        cursor.execute(update_query, (schedule, templestay_id))
        connection.commit()
        print(f"Templestay ID {templestay_id}: 일정 업데이트 완료")
    except mysql.connector.Error as err:
        print(f"일정 업데이트 오류: {err}")
    finally:
        if cursor:
            cursor.close()

def main():
    db_config_path = "C:\\jeolloga\\data\\db_config.yaml"
    db_config = load_db_config(db_config_path)

    if not db_config:
        print("DB 설정 정보를 가져오지 못했습니다. 프로그램을 종료합니다.")
        return

    connection = connect_to_db(db_config)
    if not connection:
        print("DB 연결 실패. 프로그램을 종료합니다.")
        return

    urls_with_ids = fetch_templestay_urls(connection)
    if not urls_with_ids:
        print("업데이트할 URL이 없습니다.")
        connection.close()
        return

    for templestay_id, url in urls_with_ids:
        try:
            print(f"크롤링 중: {url} (Templestay ID: {templestay_id})")
            response = requests.get(url)
            response.raise_for_status()
            soup = BeautifulSoup(response.text, "html.parser")

            schedule = crawl_schedule_data(soup)
            if schedule:
                update_schedule_in_db(connection, templestay_id, schedule)
        except Exception as e:
            print(f"크롤링 실패 (Templestay ID: {templestay_id}): {e}")

    connection.close()

if __name__ == "__main__":
    main()

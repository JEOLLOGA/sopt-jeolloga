import mysql.connector
import yaml
import requests
from bs4 import BeautifulSoup
import re
import json
from collections import OrderedDict

def load_db_config(file_path):
    with open(file_path, "r", encoding="utf-8") as file:
        config = yaml.safe_load(file)
        return config.get("database")

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

def fetch_last_id(connection):
    try:
        cursor = connection.cursor()
        query = "SELECT MAX(id) FROM templestay"
        cursor.execute(query)
        result = cursor.fetchone()
        last_id = result[0] if result and result[0] else 0
        cursor.close()
        return last_id
    
    except mysql.connector.Error as err:
        print(f"마지막 ID 가져오기 오류: {err}")
        return 0

def fetch_urls_with_ids(connection):
    try:
        cursor = connection.cursor()
        query = "SELECT id, templestay_url FROM url ORDER BY id ASC"
        cursor.execute(query)
        urls = [(row[0], row[1]) for row in cursor.fetchall()]
        cursor.close()
        return urls
    except mysql.connector.Error as err:
        print(f"URL 데이터 가져오기 오류: {err}")
        return []

def crawl_data(url, url_id):
    try:
        response = requests.get(url)
        response.raise_for_status()
        soup = BeautifulSoup(response.text, "html.parser")

        templestay_name = soup.select_one(".page-name h1").text
        templestay_name = re.search(r"]\s*(.+)", templestay_name).group(1)

        phone_number_element = soup.select_one(".page-tag a[href^='tel:']")
        phone_number = phone_number_element.text if phone_number_element else None

        temple_name = soup.select_one(".page-name h1").text
        temple_name = re.search(r"\[(.+?)\]", temple_name).group(1)

        introduction = extract_introduction(soup)

        schedule = crawl_schedule_data(soup)

        return {
            "templestay_name": templestay_name,
            "phone_number": phone_number,
            "introduction": introduction,
            "temple_name": temple_name,
            "schedule": schedule,
            "url_id": url_id
        }

    except Exception as e:
        print(f"크롤링 실패 (URL: {url}): {e}")
        return None


def extract_introduction(soup):
    try:
        introduction_data = {}

        title_element = soup.select_one("h4")
        title = title_element.text.strip() if title_element else "null"

        description_element = title_element.find_next("p") if title_element else None
        description = description_element.text if description_element else "null"

        introduction_data[title] = description

        return json.dumps(introduction_data, ensure_ascii=False)

    except Exception as e:
        print(f"소개 정보 크롤링 실패: {e}")
        return json.dumps({"error": "데이터를 가져오지 못했습니다."}, ensure_ascii=False)


def crawl_schedule_data(soup):
    try:
        schedule = OrderedDict()
        day_sections = soup.select(".temple-description h4.bullet")
        
        for day_title_element in day_sections:
            day_title = day_title_element.text.strip() if day_title_element else "null"
            table_element = day_title_element.find_next("table") if day_title_element else None
            if not table_element:
                continue

            day_schedule = OrderedDict()
            rows = table_element.select("tbody tr")
            for row in rows:
                cells = row.select("td")
                if len(cells) >= 2:
                    time_slot = cells[0].text.strip()
                    activity = cells[1].text.strip()
                    day_schedule[time_slot] = activity

            schedule[day_title] = day_schedule

        return json.dumps(schedule, ensure_ascii=False)

    except Exception as e:
        print(f"일정 크롤링 실패: {e}")
        return "{}"

def update_schedule_and_introduction(connection, data):
    try:
        cursor = connection.cursor()

        check_query = "SELECT COUNT(*) FROM templestay WHERE templestay_name = %s"
        cursor.execute(check_query, (data["templestay_name"],))
        result = cursor.fetchone()

        if result[0] > 0:
            print(f"중복된 templestay_name ({data['templestay_name']})이 존재하여 업데이트하지 않음.")
        else:
            new_id = fetch_last_id(connection) + 1
            insert_query = """
                INSERT INTO templestay (id, templestay_name, phone_number, introduction, temple_name, schedule)
                VALUES (%s, %s, %s, %s, %s, %s)
            """
            cursor.execute(insert_query, (
                new_id,
                data["templestay_name"],
                data["phone_number"],
                data["introduction"],
                data["temple_name"],
                data["schedule"]
            ))
            connection.commit()
            print(f"새로운 데이터 삽입 완료 (templestay_name: {data['templestay_name']})")

    except mysql.connector.Error as err:
        print(f"업데이트/삽입 오류 발생: {err}")

    finally:
        cursor.close()

def sequential_crawling_and_saving(urls_with_ids, connection):
    for url_id, url in urls_with_ids:
        print(f"크롤링 중: {url} (URL ID: {url_id})")
        data = crawl_data(url, url_id)
        if data:
            update_schedule_and_introduction(connection, data)

db_config_path = "C:\\jeolloga\\data\\db_config.yaml"
db_config = load_db_config(db_config_path)

connection = connect_to_db(db_config)
if not connection:
    print("DB 연결 실패. 프로그램을 종료합니다.")
    exit()

urls_with_ids = fetch_urls_with_ids(connection)
if not urls_with_ids:
    print("크롤링할 URL 데이터가 없습니다.")
    connection.close()
    exit()

sequential_crawling_and_saving(urls_with_ids, connection)
connection.close()

import mysql.connector
import yaml
import requests
from bs4 import BeautifulSoup
import re
import json

def load_db_config(file_path):
    try:
        with open(file_path, "r", encoding="utf-8") as file:
            config = yaml.safe_load(file)
            if not config or "database" not in config:
                raise ValueError("YAML 파일에 'database' 키가 없습니다.")
            return config.get("database")
    except Exception as e:
        print(f"YAML 파일 로드 오류: {e}")
        return None

def connect_to_db(config):
    try:
        connection = mysql.connector.connect(
            host=config["host"],
            user=config["user"],
            password=config["password"],
            database=config["name"]
        )
        print("DB 연결 성공")
        return connection
    except mysql.connector.Error as err:
        print(f"DB 연결 오류: {err}")
        return None

def fetch_urls(connection):
    try:
        cursor = connection.cursor()
        query = "SELECT templestay_url FROM url ORDER BY id ASC"
        cursor.execute(query)
        urls = [row[0] for row in cursor.fetchall()]
        cursor.close()
        return urls
    except mysql.connector.Error as err:
        print(f"URL 데이터 가져오기 오류: {err}")
        return []

def clean_text(text):
    return re.sub(r"\\r|\\n|\\t|\s+", " ", text).strip()

def crawl_introduction(url):
    try:
        response = requests.get(url)
        response.raise_for_status()

        soup = BeautifulSoup(response.text, "html.parser")

        h4_element = soup.select_one(".page-title + h4")
        h4_text = h4_element.text if h4_element else ""
        h4_text = clean_text(h4_text)

        introduction_element = soup.select_one(".page-content p")
        introduction = introduction_element.text if introduction_element else ""
        introduction = clean_text(introduction)

        return {
            "url": url,
            "data": json.dumps([h4_text, introduction], ensure_ascii=False)
        }

    except requests.exceptions.RequestException as e:
        print(f"요청 오류: {e}")
        return None
    except Exception as e:
        print(f"크롤링 오류: {e}")
        return None

def crawl_data(url):
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

        introduction_data = crawl_introduction(url)

        schedule = crawl_schedule_data(soup)

        return {
            "templestay_name": templestay_name,
            "phone_number": phone_number,
            "introduction": introduction_data["data"] if introduction_data else None,
            "temple_name": temple_name,
            "schedule": schedule
        }

    except requests.exceptions.RequestException as e:
        print(f"요청 오류: {e}")
        return None
    except Exception as e:
        print(f"크롤링 오류: {e}")
        return None

def crawl_schedule_data(soup):
    try:
        schedule = {}
        day_sections = soup.select(".temple-description h4.bullet")

        for day_title_element in day_sections:
            day_title = day_title_element.text.strip() if day_title_element else "null"

            table_element = day_title_element.find_next("table") if day_title_element else None
            if not table_element:
                continue

            day_schedule = {}
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
        print(f"Schedule 크롤링 오류: {e}")
        return "{}"

db_config_path = "C:\\jeolloga\\data\\db_config.yaml"

db_config = load_db_config(db_config_path)

if not db_config:
    print("DB 설정 로드 실패. 프로그램 종료")
    exit()

connection = connect_to_db(db_config)

if not connection:
    print("DB 연결 실패. 프로그램 종료")
    exit()

urls = fetch_urls(connection)

if not urls:
    print("URL 데이터 없음. 프로그램 종료")
    connection.close()
    exit()

print("크롤링 결과 (최대 10개):")
for index, url in enumerate(urls[:10]):
    print(f"크롤링 중: {url}")
    crawled_data = crawl_data(url)
    if crawled_data:
        print(crawled_data)
    else:
        print(f"크롤링 실패: {url}")

connection.close()

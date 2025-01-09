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
        query = "SELECT templestay_url FROM url" 
        cursor.execute(query)
        urls = [row[0] for row in cursor.fetchall()]
        cursor.close()
        return urls
    except mysql.connector.Error as err:
        print(f"URL 데이터 가져오기 오류: {err}")
        return []

def save_data_to_db(connection, data):
    try:
        cursor = connection.cursor()
        
        check_query = "SELECT COUNT(*) FROM templestay WHERE templestay_name = %s"
        cursor.execute(check_query, (data["templestay_name"],))
        if cursor.fetchone()[0] > 0:
            print(f"템플스테이 '{data['templestay_name']}'은 이미 존재합니다.")
            cursor.close()
            return
        
        insert_query = """
            INSERT INTO templestay (templestay_name, phone_number, templestay_price, introduction, temple_name, schedule)
            VALUES (%s, %s, %s, %s, %s, %s)
        """
        values = (
            data["templestay_name"],
            data["phone_number"],
            data["templestay_price"],
            data["introduction"],
            data["temple_name"],
            data["schedule"]
        )
        cursor.execute(insert_query, values)
        connection.commit()
        print(f"데이터 저장 완료: {data['templestay_name']}")
        cursor.close()
    except mysql.connector.Error as err:
        print(f"DB 저장 오류: {err}")



def crawl_data(url):
    try:
        response = requests.get(url)
        response.raise_for_status()

        soup = BeautifulSoup(response.text, "html.parser")

        templestay_name = soup.select_one(".page-name h1").text
        templestay_name = re.search(r"]\s*(.+)", templestay_name).group(1)

        phone_number_element = soup.select_one(".page-tag a[href^='tel:']")
        phone_number = phone_number_element.text if phone_number_element else None

        price_row = soup.select("tr:nth-of-type(2) td.work-info")
        templestay_price = price_row[1].text if len(price_row) > 1 else None

        introduction_element = soup.select_one(".page-content p")
        introduction = introduction_element.text if introduction_element else None

        temple_name = soup.select_one(".page-name h1").text
        temple_name = re.search(r"\[(.+?)\]", temple_name).group(1)

        schedule_rows = soup.select(".temple-description tr")
        schedule = {}
        for row in schedule_rows:
            time_slot_element = row.select_one(".work-title")
            activity_element = row.select_one("td:nth-child(2)")
            if time_slot_element and activity_element:
                time_slot = time_slot_element.text
                activity = activity_element.text
                schedule[time_slot] = activity

        return {
            "templestay_name": templestay_name,
            "phone_number": phone_number,
            "templestay_price": templestay_price,
            "introduction": introduction,
            "temple_name": temple_name,
            "schedule": json.dumps(schedule, ensure_ascii=False)
        }

    except requests.exceptions.RequestException as e:
        print(f"요청 오류: {e}")
        return None
    except Exception as e:
        print(f"크롤링 오류: {e}")
        return None

db_config_path = "C:\\jeolloga\\crawling\\db_config.yaml"

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
    exit()

for url in urls:
    print(f"크롤링 중: {url}")
    crawled_data = crawl_data(url)
    if crawled_data:
        save_data_to_db(connection, crawled_data)
    else:
        print(f"크롤링 실패: {url}")

connection.close()
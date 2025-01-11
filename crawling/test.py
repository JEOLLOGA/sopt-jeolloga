import mysql.connector
import yaml
import requests
from bs4 import BeautifulSoup
import re

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

def crawl_data(url):
    try:
        response = requests.get(url)
        if response.status_code != 200:
            print(f"URL 응답 오류: {response.status_code} - {url}")
            return None

        soup = BeautifulSoup(response.content, "html.parser")
        page_tags = soup.find_all("div", class_="page-tag")

        # 정규식을 사용하여 콤마 이전의 값 추출
        extracted_data = []
        for tag in page_tags:
            match = re.search(r"^(.*?),", tag.text.strip())  # 콤마 이전 값 추출
            if match:
                extracted_data.append(match.group(1).strip())

        return extracted_data
    except Exception as e:
        print(f"크롤링 오류: {e}")
        return None

def save_data_to_db(connection, data, url):
    try:
        cursor = connection.cursor()
        for item, code in data:
            query = "INSERT INTO crawled_data (data, code, url) VALUES (%s, %s, %s)"
            cursor.execute(query, (item, code, url))
        connection.commit()
        print(f"{len(data)}개의 데이터가 DB에 저장되었습니다.")
        cursor.close()
    except mysql.connector.Error as err:
        print(f"DB 저장 오류: {err}")

# 유형 매핑
TYPE_MAPPING = {
    "당일형": 1,
    "휴식형": 2,
    "체험형": 3
}

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
    connection.close()
    exit()

count = 0
for url in urls:
    if count >= 10:  # 10개 URL만 크롤링
        break
    print(f"크롤링 중: {url}")
    crawled_data = crawl_data(url)
    if crawled_data:
        for data in crawled_data:
            code = TYPE_MAPPING.get(data, 0)  # 매핑되지 않은 경우 0
            print(f"크롤링 결과: {data} {code}")  # 요구 형식으로 출력
        count += 1
    else:
        print(f"크롤링 실패: {url}")

connection.close()

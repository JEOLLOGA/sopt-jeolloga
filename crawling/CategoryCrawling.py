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
        query = "SELECT templestay_url, templestay_id FROM url ORDER BY id ASC"
        cursor.execute(query)
        urls = cursor.fetchall()
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

        extracted_data = []
        for tag in page_tags:
            match = re.search(r"^(.*?),\s*([가-힣]+)", tag.text.strip())
            if match:
                type_value = match.group(1).strip()
                region_value = match.group(2).strip()

                price_row = soup.select("tr:nth-of-type(2) td.work-info")
                templestay_price = price_row[1].text if len(price_row) > 1 else None

                extracted_data.append((type_value, region_value, templestay_price))

        return extracted_data
    except Exception as e:
        print(f"크롤링 오류: {e}")
        return None

TYPE_MAPPING = {
    "당일형": 1,
    "휴식형": 2,
    "체험형": 3
}

REGION_MAPPING = {
    "강원": 1,
    "경기": 2,
    "경남": 3,
    "경북": 4,
    "광주": 5,
    "대구": 6,
    "대전": 7,
    "부산": 8,
    "서울": 9,
    "인천": 10,
    "전남": 11,
    "전북": 12,
    "제주": 13,
    "충남": 14,
    "충북": 15
}

def price_to_code(price_text):
    """
    가격 문자열에서 콤마와 '원'을 제거하고 숫자만 반환하는 함수
    예시: "10,000원" -> 10000
    """
    if price_text:
        cleaned_price = re.sub(r"[^\d]", "", price_text)
        return int(cleaned_price)
    return 0

def update_data_in_db(connection, data, templestay_id):
    try:
        cursor = connection.cursor()

        for type_value, region_value, templestay_price in data:
            type_code = TYPE_MAPPING.get(type_value, 0)
            region_code = REGION_MAPPING.get(region_value, 0)
            price_code = price_to_code(templestay_price)

            update_query = """
                UPDATE category
                SET type = %s, region = %s, price = %s
                WHERE templestay_id = %s AND type = %s AND region = %s
            """
            cursor.execute(update_query, (type_code, region_code, price_code, templestay_id, type_code, region_code))
            connection.commit()

            print(f"카테고리 테이블에 데이터 업데이트 완료: 템플스테이 ID={templestay_id}, 유형={type_value}, 지역={region_value}, 가격={templestay_price}")

        cursor.close()
    except mysql.connector.Error as err:
        print(f"DB 업데이트 오류: {err}")

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

for url, templestay_id in urls:
    print(f"크롤링 중: {url}")
    crawled_data = crawl_data(url)
    if crawled_data:
        update_data_in_db(connection, crawled_data, templestay_id)
    else:
        print(f"크롤링 실패: {url}")

connection.close()

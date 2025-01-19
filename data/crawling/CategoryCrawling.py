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

def is_templestay_id_exist(connection, templestay_id):
    try:
        cursor = connection.cursor()
        query = "SELECT EXISTS(SELECT 1 FROM category WHERE templestay_id = %s)"
        cursor.execute(query, (templestay_id,))
        exists = cursor.fetchone()[0]
        cursor.close()
        return exists
    except mysql.connector.Error as err:
        print(f"templestay_id 존재 여부 확인 오류: {err}")
        return False

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

REGION_BINARY_MAPPING = {
    "강원": 0b000000000000001,
    "경기": 0b000000000000010,
    "경남": 0b000000000000100,
    "경북": 0b000000000001000,
    "광주": 0b000000000010000,
    "대구": 0b000000000100000,
    "대전": 0b000000001000000,
    "부산": 0b000000010000000,
    "서울": 0b000000100000000,
    "인천": 0b000001000000000,
    "전남": 0b000010000000000,
    "전북": 0b000100000000000,
    "제주": 0b001000000000000,
    "충남": 0b010000000000000,
    "충북": 0b100000000000000
}

TYPE_BINARY_MAPPING = {
    "당일형": 0b001,
    "휴식형": 0b010,
    "체험형": 0b100
}

def price_to_code(price_text):
    """
    가격 문자열에서 콤마와 '원'을 제거하고 숫자만 반환하는 함수
    예시: "10,000원" -> 10000
    """
    if price_text:
        cleaned_price = re.sub(r"[^\d]", "", price_text)
        return int(cleaned_price)  # 숫자만 반환
    return 0

def save_data_to_db(connection, data, templestay_id):
    try:
        cursor = connection.cursor()

        for type_value, region_value, templestay_price in data:
            cursor.execute("SELECT IFNULL(MAX(id), 0) FROM category")
            max_id = cursor.fetchone()[0]
            next_id = max_id + 1

            type_code = TYPE_BINARY_MAPPING.get(type_value, 0)
            region_code = REGION_BINARY_MAPPING.get(region_value, 0)
            price_code = price_to_code(templestay_price)

            category_query = """
                INSERT INTO category (id, type, region, price, templestay_id)
                VALUES (%s, %s, %s, %s, %s)
            """
            cursor.execute(category_query, (next_id, type_code, region_code, price_code, templestay_id))
            print(f"카테고리 테이블에 데이터 저장 완료: ID={next_id}, 템플스테이 ID={templestay_id}, 유형={type_value}({type_code:b}), 지역={region_value}({region_code:b}), 가격={templestay_price}")

        connection.commit()

        cursor.close()
    except mysql.connector.Error as err:
        print(f"DB 저장 오류: {err}")

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

for url, templestay_id in urls:
    if is_templestay_id_exist(connection, templestay_id):
        print(f"템플스테이 ID={templestay_id}는 이미 존재합니다. 크롤링 건너뜁니다.")
        continue

    print(f"크롤링 중: {url}")
    crawled_data = crawl_data(url)
    if crawled_data:
        save_data_to_db(connection, crawled_data, templestay_id)
    else:
        print(f"크롤링 실패: {url}")

connection.close()
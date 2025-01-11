import mysql.connector
import yaml
import requests
from bs4 import BeautifulSoup

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

def fetch_urls_with_ids(connection):
    try:
        cursor = connection.cursor()
        query = "SELECT templestay_id, templestay_url FROM url ORDER BY id ASC"
        cursor.execute(query)
        urls_with_ids = cursor.fetchall()
        cursor.close()
        return urls_with_ids
    except mysql.connector.Error as err:
        print(f"URL 데이터 가져오기 오류: {err}")
        return []

def save_images_to_db(connection, templestay_id, img_urls):
    try:
        cursor = connection.cursor()

        cursor.execute("SELECT MAX(id) FROM templestay_image")
        max_id_result = cursor.fetchone()
        next_id = (max_id_result[0] + 1) if max_id_result[0] else 1 
        for img_url in img_urls:
            insert_query = """
                INSERT INTO templestay_image (id, templestay_id, img_url) 
                VALUES (%s, %s, %s)
            """
            cursor.execute(insert_query, (next_id, templestay_id, img_url))
            next_id += 1 

        connection.commit()
        print(f"이미지 저장 완료: 템플스테이 ID={templestay_id}, 이미지 개수={len(img_urls)}")
        cursor.close()
    except mysql.connector.Error as err:
        print(f"이미지 저장 오류: {err}")

def crawl_images(url):
    try:
        response = requests.get(url)
        response.raise_for_status()
        soup = BeautifulSoup(response.text, "html.parser")
        
        img_tags = soup.select(".templeslider img")
        img_urls = [tag["src"] for tag in img_tags if "src" in tag.attrs]
        
        return img_urls
    except requests.exceptions.RequestException as e:
        print(f"요청 오류: {e}")
        return []
    except Exception as e:
        print(f"크롤링 오류: {e}")
        return []

db_config_path = "C:\\jeolloga\\crawling\\db_config.yaml"

db_config = load_db_config(db_config_path)
if not db_config:
    print("DB 설정 로드 실패. 프로그램 종료")
    exit()

connection = connect_to_db(db_config)
if not connection:
    print("DB 연결 실패. 프로그램 종료")
    exit()

urls_with_ids = fetch_urls_with_ids(connection)
if not urls_with_ids:
    print("URL 데이터 없음. 프로그램 종료")
    connection.close()
    exit()

for templestay_id, url in urls_with_ids:
    print(f"크롤링 중: 템플스테이 ID={templestay_id}, URL={url}")
    img_urls = crawl_images(url)
    if img_urls:
        save_images_to_db(connection, templestay_id, img_urls)
    else:
        print(f"이미지 없음 또는 크롤링 실패: 템플스테이 ID={templestay_id}, URL={url}")

connection.close()

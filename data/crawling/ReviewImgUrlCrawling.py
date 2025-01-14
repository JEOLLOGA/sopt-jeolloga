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

def fetch_reviews_with_links(connection):
    try:
        cursor = connection.cursor()
        query = "SELECT id, review_link FROM review WHERE review_img_url IS NULL"
        cursor.execute(query)
        reviews = cursor.fetchall()
        cursor.close()
        return reviews
    except mysql.connector.Error as err:
        print(f"리뷰 데이터 가져오기 오류: {err}")
        return []

def save_image_to_db(connection, review_id, image_url):
    try:
        cursor = connection.cursor()
        update_query = "UPDATE review SET review_img_url = %s WHERE id = %s"
        cursor.execute(update_query, (image_url, review_id))
        connection.commit()
        print(f"이미지 저장 완료: 리뷰 ID={review_id}, 이미지 URL={image_url}")
        cursor.close()
    except mysql.connector.Error as err:
        print(f"이미지 저장 오류: {err}")

def fetch_image_from_link(review_link):
    try:
        response = requests.get(review_link)
        response.raise_for_status()
        document = BeautifulSoup(response.text, "html.parser")

        iframe = document.select_one("iframe#mainFrame")
        if iframe:
            iframe_src = iframe.get("src")
            if not iframe_src:
                print(f"Iframe source not found for link {review_link}")
                return None

            iframe_response = requests.get("https://blog.naver.com" + iframe_src)
            iframe_response.raise_for_status()
            iframe_document = BeautifulSoup(iframe_response.text, "html.parser")

            image_elements = iframe_document.select("div.se-module.se-module-image img")
            for img_tag in image_elements:
                image_url = img_tag.get("data-lazy-src") or img_tag.get("src")
                if image_url:
                    return image_url

        print(f"No iframe found for link {review_link}")
        return None

    except requests.exceptions.RequestException as e:
        print(f"요청 오류: {e}")
        return None
    except Exception as e:
        print(f"크롤링 오류: {e}")
        return None

db_config_path = "C:\\jeolloga\\data\\db_config.yaml"

db_config = load_db_config(db_config_path)
if not db_config:
    print("DB 설정 로드 실패. 프로그램 종료")
    exit()

connection = connect_to_db(db_config)
if not connection:
    print("DB 연결 실패. 프로그램 종료")
    exit()

reviews = fetch_reviews_with_links(connection)
if not reviews:
    print("리뷰 데이터 없음. 프로그램 종료")
    connection.close()
    exit()

for review_id, review_link in reviews:
    if not review_link:
        print(f"리뷰 ID={review_id}에 링크가 없습니다. 건너뜁니다.")
        continue

    print(f"크롤링 중: 리뷰 ID={review_id}, 링크={review_link}")
    image_url = fetch_image_from_link(review_link)
    if image_url:
        save_image_to_db(connection, review_id, image_url)
    else:
        print(f"이미지 없음 또는 크롤링 실패: 리뷰 ID={review_id}, 링크={review_link}")

connection.close()
print("DB 연결 닫힘")

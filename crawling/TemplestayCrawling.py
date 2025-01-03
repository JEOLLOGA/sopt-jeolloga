from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.options import Options
from bs4 import BeautifulSoup
import mysql.connector
import yaml
import re
import time

def load_db_config(file_path):
    try:
        with open(file_path, "r", encoding="utf-8") as file:
            config = yaml.safe_load(file)
            return config.get("database") if config else None
    except Exception as e:
        print(f"YAML 파일 로드 오류: {e}")
        return None

# DB 연결
def connect_to_db(config):
    try:
        connection = mysql.connector.connect(
            host=config["host"],
            user=config["user"],
            password=config["password"],
            database=config["database"]
        )
        return connection
    except mysql.connector.Error as err:
        print(f"DB 연결 오류: {err}")
        return None

# URL 데이터 가져오기
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

# 크롤링 함수
def crawl_data(url):
    try:
        chrome_options = Options()
        chrome_options.add_argument("--headless")
        chrome_options.add_argument("--disable-gpu")
        
        driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=chrome_options)
        driver.get(url)
        time.sleep(2)

        soup = BeautifulSoup(driver.page_source, "html.parser")
        driver.quit()

    except Exception as e:
        print(f"크롤링 오류: {e}")
        return {"": str(e)}

# DB 저장
def save_data_to_db(connection, data):
    try:
        cursor = connection.cursor()
        cursor.close()
    except mysql.connector.Error as err:
        print(f"{err}")

# Main 실행
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
    crawled_data = crawl_data(url)
    save_data_to_db(connection, crawled_data)

connection.close()

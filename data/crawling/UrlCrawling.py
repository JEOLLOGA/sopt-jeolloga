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


def save_templestay_data_to_db(connection, templestay_url):
    try:
        cursor = connection.cursor()

        check_query = "SELECT COUNT(*) FROM url WHERE templestay_url = %s"
        cursor.execute(check_query, (templestay_url,))
        count = cursor.fetchone()[0]

        if count == 0:
            get_max_id_query = "SELECT MAX(id) FROM url"
            cursor.execute(get_max_id_query)
            max_id = cursor.fetchone()[0]
            next_id = (max_id + 1) if max_id else 1 

            insert_query = "INSERT INTO url (id, templestay_url) VALUES (%s, %s)"
            cursor.execute(insert_query, (next_id, templestay_url))
            connection.commit()
            print(f"데이터 저장: ID='{next_id}', URL='{templestay_url}'")
        else:
            print(f"중복된 URL: '{templestay_url}', 저장하지 않음.")

    except mysql.connector.Error as e:
        print(f"데이터베이스 오류: {e}")
    finally:
        cursor.close()

def extract_templestay_data_with_paging(url, connection):
    chrome_options = Options()
    chrome_options.add_argument("--headless")
    chrome_options.add_argument("--disable-gpu")
    chrome_options.add_argument("--no-sandbox")

    driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=chrome_options)

    try:
        driver.get(url)
        driver.implicitly_wait(10)

        while True:
            html = driver.page_source
            soup = BeautifulSoup(html, "html.parser")

            listings = soup.find("div", {"id": "et-listings"})
            if listings:
                items = listings.find_all("li", {"class": "et-active-listing clearfix"})
                for item in items:
                    link_tag = item.find("a", {"class": "readmore-link"})
                    templestay_url = None
                    if link_tag and link_tag.get("href"):
                        base_url = "https://www.templestay.com"
                        templestay_url = base_url + link_tag["href"]

                    if templestay_url:
                        save_templestay_data_to_db(connection, templestay_url)
                    else:
                        print(f"데이터 누락 - URL: {templestay_url}")

            # 다음 페이지로 이동
            try:
                next_button = driver.find_element(By.ID, "content_LinkNext")
                if "aspNetDisabled" in next_button.get_attribute("class"):
                    print("마지막 페이지에 도달했습니다.")
                    break
                else:
                    next_button.click()
                    time.sleep(2)
            except Exception as e:
                print(f"다음 페이지 버튼을 찾을 수 없습니다: {e}")
                break

    finally:
        driver.quit()


db_config_path = "C:\\jeolloga\\data\\db_config.yaml"
db_config = load_db_config(db_config_path)

if not db_config:
    print("DB 설정 로드 실패. 프로그램 종료")
    exit()

try:
    connection = mysql.connector.connect(
        host=db_config["host"],
        user=db_config["user"],
        password=db_config["password"],
        database=db_config["name"]
    )
    print("DB 연결 성공")

    url = "https://www.templestay.com/reserv_search.aspx"
    extract_templestay_data_with_paging(url, connection)

finally:
    if 'connection' in locals() and connection.is_connected():
        connection.close()
        print("DB 연결 종료")

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


def insert_or_skip_youtube_link(connection, temple_name, youtube):
    try:
        cursor = connection.cursor()

        check_query = "SELECT id FROM templestay WHERE TRIM(temple_name) = %s"
        cursor.execute(check_query, (temple_name,))
        results = cursor.fetchall()

        if results:
            update_query = "UPDATE templestay SET youtube = %s WHERE TRIM(temple_name) = %s"
            cursor.execute(update_query, (youtube, temple_name))
            print(f"[업데이트] temple_name: {temple_name}, youtube: {youtube}, 총 개수: {cursor.rowcount}")
        else:
            print(f"temple_name: {temple_name}, youtube: {youtube} 사찰이 데이터베이스에 존재하지 않음")

        connection.commit()

    except mysql.connector.Error as e:
        print(f"DB 처리 오류: {e}")
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

            listings = soup.find_all("div", {"class": "listing-text"})
            for listing in listings:
                h4_tag = listing.find("h4")
                text_content = h4_tag.text.strip() if h4_tag else ""
                temple_name_match = re.search(r"\[([^\]]+)\]", text_content)
                if temple_name_match:
                    temple_name = temple_name_match.group(1).strip()
                else:
                    temple_name = None

                youtube_tag = listing.find("a", {"href": True})
                youtube = youtube_tag["href"] if youtube_tag and "youtube.com" in youtube_tag["href"] else None

                if temple_name and youtube:
                    insert_or_skip_youtube_link(connection, temple_name, youtube)

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


db_config_path = "C:\\jeolloga\\crawling\\db_config.yaml"
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

    url = "https://www.templestay.com/temple_search.aspx"
    extract_templestay_data_with_paging(url, connection)

finally:
    if 'connection' in locals() and connection.is_connected():
        connection.close()
        print("DB 연결 종료")

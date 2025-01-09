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
            return config.get("database")
    except Exception as e:
        print(f"YAML 파일 로드 오류: {e}")
        return None


def test_insert_or_skip_address(cursor, templestay_name, address):
    """address가 이미 존재하면 저장하지 않고 테스트 출력"""
    try:
        # address 확인
        check_query = "SELECT COUNT(*) FROM templestay WHERE address = %s"
        cursor.execute(check_query, (address,))
        count = cursor.fetchone()[0]

        if count > 0:
            print(f"[테스트 - 건너뛰기] 이미 존재하는 주소: 사찰명='{templestay_name}', 주소='{address}'")
        else:
            print(f"[테스트 - 삽입 가능] 사찰명='{templestay_name}', 주소='{address}'")
    except mysql.connector.Error as e:
        print(f"데이터베이스 처리 오류: {e}")


def extract_and_test_templestay_data(url, cursor):
    """템플스테이 데이터 추출 및 테스트"""
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
            if not listings:
                print("더 이상 처리할 데이터가 없습니다.")
                break

            for listing in listings:
                h3_tag = listing.find("h3")
                templestay_name = None
                if h3_tag:
                    match = re.search(r"\[(.+?)\]", h3_tag.text)
                    if match:
                        templestay_name = match.group(1)

                p_tags = listing.find_all("p")
                address = None
                if len(p_tags) > 1:
                    raw_text = p_tags[1].find(text=True)
                    address = raw_text.strip() if raw_text else None

                if templestay_name and address:
                    print(f"[테스트 - 처리 중] 사찰명='{templestay_name}', 주소='{address}'")
                    test_insert_or_skip_address(cursor, templestay_name, address)
                else:
                    print(f"[테스트 - 데이터 누락] 사찰명: {templestay_name}, 주소: {address}")

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


db_config_path = "C:\\jeolloga\\crawling\\db_config.yaml"
db_config = load_db_config(db_config_path)

if not db_config:
    print("DB 설정 로드 실패. 프로그램 종료")
    exit()

try:
    conn = mysql.connector.connect(
        host=db_config["host"],
        user=db_config["user"],
        password=db_config["password"],
        database=db_config["name"]
    )
    cursor = conn.cursor()
    print("DB 연결 성공 (테스트 모드)")

    url = "https://www.templestay.com/reserv_search.aspx"
    extract_and_test_templestay_data(url, cursor)

finally:
    if 'cursor' in locals() and cursor:
        cursor.close()
    if 'conn' in locals() and conn.is_connected():
        conn.close()
        print("DB 연결 종료")

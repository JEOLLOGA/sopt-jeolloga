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

# DB 설정 로드
def load_db_config(file_path):
    try:
        with open(file_path, "r", encoding="utf-8") as file:
            config = yaml.safe_load(file)
            return config.get("database") if config else None
    except Exception as e:
        print(f"YAML 파일 로드 오류: {e}")
        return None

# 주소 업데이트 함수
def update_address_in_db(templestay_name, address):
    try:
        conn = mysql.connector.connect(
            host=db_config["host"],
            user=db_config["user"],
            password=db_config["password"],
            database=db_config["name"]
        )
        cursor = conn.cursor()

        # temple_name 존재 여부 확인
        check_query = "SELECT COUNT(*) FROM templestay WHERE temple_name = %s"
        cursor.execute(check_query, (templestay_name,))
        count = cursor.fetchone()[0]

        if count > 0:
            # address 업데이트
            update_query = "UPDATE templestay SET address = %s WHERE temple_name = %s"
            cursor.execute(update_query, (address, templestay_name))
            conn.commit()
            print(f"주소 업데이트 성공: 사찰명='{templestay_name}', 주소='{address}'")
        else:
            print(f"사찰명 '{templestay_name}'은 데이터베이스에 존재하지 않습니다. 주소 업데이트 불가.")

    except mysql.connector.Error as e:
        print(f"데이터베이스 오류: {e}")
    finally:
        if 'cursor' in locals() and cursor:
            cursor.close()
        if 'conn' in locals() and conn.is_connected():
            conn.close()

# 모든 데이터를 처리하고 업데이트
def extract_and_update_all_templestay_data(url):
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
                # 템플스테이명 추출
                h3_tag = listing.find("h3")
                templestay_name = None
                if h3_tag:
                    match = re.search(r"\[(.+?)\]", h3_tag.text)  # 대괄호 안의 값 추출
                    if match:
                        templestay_name = match.group(1)

                # 두 번째 <p> 태그 값 추출 (순수 텍스트만, <br> 태그 제외)
                p_tags = listing.find_all("p")
                address = None
                if len(p_tags) > 1:  # 두 번째 <p> 태그가 존재하는 경우
                    raw_text = p_tags[1].find(text=True)  # <br> 이전의 텍스트만 가져옴
                    address = raw_text.strip() if raw_text else None

                if templestay_name and address:
                    print(f"처리 중: 사찰명='{templestay_name}', 주소='{address}'")
                    update_address_in_db(templestay_name, address)
                else:
                    print(f"데이터 누락 - 사찰명: {templestay_name}, 주소: {address}")

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

# Main 실행
db_config_path = "C:\\jeolloga\\crawling\\db_config.yaml"
db_config = load_db_config(db_config_path)

if not db_config:
    print("DB 설정 로드 실패. 프로그램 종료")
    exit()

url = "https://www.templestay.com/reserv_search.aspx"
extract_and_update_all_templestay_data(url)

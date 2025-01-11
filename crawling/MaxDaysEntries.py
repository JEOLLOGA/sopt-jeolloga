import mysql.connector
import yaml
import json

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

def fetch_schedules(connection):
    try:
        cursor = connection.cursor()
        query = """
            SELECT id, schedule
            FROM templestay
            WHERE schedule IS NOT NULL AND JSON_VALID(schedule) = 1
        """
        cursor.execute(query)
        schedules = cursor.fetchall()
        cursor.close()
        return schedules
    except mysql.connector.Error as err:
        print(f"DB 데이터 가져오기 실패: {err}")
        return []

# JSON 데이터를 분석하여 최대 며칠차와 최대 줄 수 계산
def analyze_schedules(schedules):
    max_days = 0
    max_entries = 0
    max_days_id = None
    max_entries_id = None

    for record in schedules:
        templestay_id, schedule_json = record
        try:
            schedule = json.loads(schedule_json)
            total_days = len(schedule.keys())
            max_entries_per_day = max(len(day) for day in schedule.values()) if schedule else 0

            if total_days > max_days:
                max_days = total_days
                max_days_id = templestay_id

            if max_entries_per_day > max_entries:
                max_entries = max_entries_per_day
                max_entries_id = templestay_id

        except Exception as e:
            print(f"JSON 파싱 오류 (ID={templestay_id}): {e}")

    return max_days, max_days_id, max_entries, max_entries_id

def print_results(max_days, max_days_id, max_entries, max_entries_id):
    print(f"최대 며칠차: {max_days}일차 (템플스테이 ID={max_days_id})")
    print(f"최대 항목 수: {max_entries}줄 (템플스테이 ID={max_entries_id})")

def main():
    db_config_path = "C:\\jeolloga\\crawling\\db_config.yaml"
    db_config = load_db_config(db_config_path)
    if not db_config:
        print("DB 설정 로드 실패. 프로그램 종료")
        return

    connection = connect_to_db(db_config)
    if not connection:
        print("DB 연결 실패. 프로그램 종료")
        return

    schedules = fetch_schedules(connection)
    if not schedules:
        print("데이터가 없습니다.")
        connection.close()
        return

    max_days, max_days_id, max_entries, max_entries_id = analyze_schedules(schedules)
    print_results(max_days, max_days_id, max_entries, max_entries_id)

    connection.close()

if __name__ == "__main__":
    main()

import pandas as pd
import mysql.connector
import yaml

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

def map_to_int(value, mapping):
    return mapping.get(value.strip(), 0)

def update_category_data(connection, file_path):
    purpose_mapping = {
        "힐링": 1,
        "전통문화체험": 2,
        "심신치유": 3,
        "자기계발": 4,
        "여행 일정": 5,
        "사찰순례": 6,
        "휴식": 7,
        "호기심": 8,
        "기타": 9,
    }
    activity_mapping = {
        "발우공양": 1,
        "108배": 2,
        "디지털 디톡스": 3,
        "스님과의 차담": 4,
        "새벽 예불": 5,
        "사찰 탐방": 6,
        "염주 만들기": 7,
        "연등 만들기": 8,
        "다도": 9,
        "명상": 10,
        "산책": 11,
        "요가": 12,
        "기타": 13,
    }
    etc_mapping = {
        "절밥이 맛있는": 1,
        "단체 가능": 2,
        "TV에 나온": 3,
        "연예인이 다녀간": 4,
        "근처 관광지가 많은": 5,
        "속세와 멀어지고 싶은": 6,
        "동물친구들과 함께": 7,
    }
    cursor = None
    try:
        df = pd.read_excel(file_path)
        select_query = """
        SELECT id
        FROM templestay
        WHERE temple_name LIKE %s
        """
        update_query = """
        UPDATE category
        SET type = %s, purpose = %s, activity = %s, etc = %s
        WHERE templestay_id = %s
        """
        cursor = connection.cursor()
        for _, row in df.iterrows():
            templestay_name = row['템플스테이명']
            category_type = row['유형']
            purpose = row['목적']
            activity = row['체험']
            etc = row['기타']
            cursor.execute(select_query, (f"%{templestay_name}%",))
            result = cursor.fetchone()
            if result:
                templestay_id = result[0]
                int_purpose = map_to_int(purpose, purpose_mapping)
                int_activity = map_to_int(activity, activity_mapping)
                int_etc = map_to_int(etc, etc_mapping)
                cursor.execute(update_query, (category_type, int_purpose, int_activity, int_etc, templestay_id))
                print(f"category 데이터 삽입: {templestay_id}: {category_type}, {int_purpose}, {int_activity}, {int_etc}")
            else:
                print(f"존재하지 않는 템플스테이: '{templestay_name}'")
        connection.commit()
        print("category 데이터 업데이트 완료")
    except Exception as e:
        print(f"category 데이터 업데이트 중 오류 발생: {e}")
    finally:
        if cursor:
            cursor.close()

def main():
    db_config_path = "C:\\jeolloga\\data\\db_config.yaml"
    file_path = "C:\\Users\\didek\\OneDrive\\문서\\jeolloga-data.xlsx"
    db_config = load_db_config(db_config_path)
    if not db_config:
        print("DB 설정 로드 실패. 프로그램 종료")
        return
    connection = connect_to_db(db_config)
    if not connection:
        print("DB 연결 실패. 프로그램 종료")
        return
    try:
        update_category_data(connection, file_path)
    finally:
        connection.close()
        print("DB 연결 종료")

if __name__ == "__main__":
    main()

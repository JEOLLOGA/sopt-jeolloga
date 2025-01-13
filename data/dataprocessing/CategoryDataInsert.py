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

PURPOSE_MAPPING = {
    "힐링": 0b00000001,
    "전통문화 체험": 0b00000010,
    "심신치유": 0b00000100,
    "자기계발": 0b00001000,
    "여행 일정": 0b00010000,
    "사찰순례": 0b00100000,
    "휴식": 0b01000000,
    "호기심": 0b10000000,
}

ACTIVITY_MAPPING = {
    "발우공양": 0b0000000000001,
    "108배": 0b0000000000010,
    "스님과의 차담": 0b0000000000100,
    "등산": 0b0000000001000,
    "새벽 예불": 0b0000000010000,
    "사찰 탐방": 0b0000000100000,
    "염주 만들기": 0b0000001000000,
    "연등 만들기": 0b0000010000000,
    "다도": 0b0000100000000,
    "명상": 0b0001000000000,
    "산책": 0b0010000000000,
    "요가": 0b0100000000000,
    "기타": 0b1000000000000,
}

ETC_MAPPING = {
    "절밥이 맛있는": 0b00000001,
    "TV에 나온": 0b00000010,
    "연예인이 다녀간": 0b00000100,
    "근처 관광지가 많은": 0b00001000,
    "속세와 멀어지고 싶은": 0b00010000,
    "단체 가능": 0b00100000,
    "동물 친구들과 함께": 0b01000000,
    "유튜브 운영 중인": 0b10000000,
}

def calculate_bitwise_value(values, mapping):
    bit_value = 0
    for value in values:
        bit_value |= mapping.get(value.strip(), 0)
    return bit_value

def update_category_data(connection, file_path):
    cursor = None
    try:
        df = pd.read_excel(file_path)

        df['목적'] = df['목적'].fillna("")
        df['체험'] = df['체험'].fillna("")
        df['기타'] = df['기타'].fillna("")

        select_query = """
            SELECT id
            FROM templestay
            WHERE templestay_name LIKE CONCAT('%', %s, '%')
        """
        update_query = """
            UPDATE category
            SET type = %s, purpose = %s, activity = %s, etc = %s
            WHERE templestay_id = %s
        """

        cursor = connection.cursor(buffered=True)

        for _, row in df.iterrows():
            templestay_name = row['템플스테이명']
            category_type = row['유형']
            purposes = row['목적'].split(",") if isinstance(row['목적'], str) else []
            activities = row['체험'].split(",") if isinstance(row['체험'], str) else []
            etcs = row['기타'].split(",") if isinstance(row['기타'], str) else []

            cursor.execute(select_query, (templestay_name,))
            results = cursor.fetchall()

            if results:
                for result in results:
                    templestay_id = result[0]

                    purpose_bits = calculate_bitwise_value(purposes, PURPOSE_MAPPING)
                    activity_bits = calculate_bitwise_value(activities, ACTIVITY_MAPPING)
                    etc_bits = calculate_bitwise_value(etcs, ETC_MAPPING)

                    cursor.execute(update_query, (category_type, purpose_bits, activity_bits, etc_bits, templestay_id))
                    print(f"category 데이터 업데이트 완료: ID={templestay_id}")
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

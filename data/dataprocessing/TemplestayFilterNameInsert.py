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

def update_templestay_name(connection, file_path):
    try:
        df = pd.read_excel(file_path)
        
        if "templestay_name" not in df.columns or "organized_name" not in df.columns:
            print("Excel 파일에 'templestay_name' 또는 'organized_name' 열이 없습니다.")
            return
        
        cursor = connection.cursor()
        
        for _, row in df.iterrows():
            templestay_name = row["templestay_name"]
            organized_name = row["organized_name"]
            
            query = """
                UPDATE templestay
                SET organized_name = %s
                WHERE templestay_name LIKE %s;
            """
            cursor.execute(query, (organized_name, f"%{templestay_name}%"))
        
        connection.commit()
        print(f"{cursor.rowcount}개의 레코드가 업데이트되었습니다.")
    
    except Exception as e:
        print(f"업데이트 중 오류 발생: {e}")
    finally:
        cursor.close()

def main():
    db_config_path = "C:\\jeolloga\\data\\db_config.yaml"
    file_path = "C:\\Users\\didek\\OneDrive\\바탕 화면\\jeolloga_templestay_name.xlsx"
    
    db_config = load_db_config(db_config_path)
    if not db_config:
        print("DB 설정 로드 실패. 프로그램 종료")
        return
    
    connection = connect_to_db(db_config)
    if not connection:
        print("DB 연결 실패. 프로그램 종료")
        return
    
    try:
        update_templestay_name(connection, file_path)
    finally:
        connection.close()
        print("DB 연결 종료")

if __name__ == "__main__":
    main()

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

def update_templestay_tags(connection, file_path):
    cursor = None
    try:
        df = pd.read_excel(file_path)
        
        update_query = """
        UPDATE templestay
        SET tag = %s
        WHERE templestay_name LIKE %s
        """
        
        cursor = connection.cursor()
        
        for _, row in df.iterrows():
            templestay_name = row['템플스테이명']
            tag = row['대표 키워드'] 
            
            if not isinstance(tag, str) or not tag.strip():
                tag = "태그 없음"
            
            print(f"Tag 데이터 삽입: '%{templestay_name}%' with tag: {tag}")
            
            cursor.execute(update_query, (tag, f"%{templestay_name}%"))
            
            if cursor.rowcount == 0:
                print(f"존재하지않는 templestay_name: '%{templestay_name}%'")
            else:
                print(f"Insert {cursor.rowcount} temple_name '%{templestay_name}%'")
        
        connection.commit()
        print("tag 업데이트 완료")
    
    except Exception as e:
        print(f"tag 업데이트 중 오류 발생: {e}")
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
        update_templestay_tags(connection, file_path)
    finally:
        connection.close()
        print("DB 연결 종료")

if __name__ == "__main__":
    main()

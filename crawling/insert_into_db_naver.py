import psycopg2
import json
import pandas as pd
import re
import os
from dotenv import load_dotenv

# 환경변수 로드
load_dotenv()

host = os.getenv("host").strip('", ')
dbname = os.getenv("dbname").strip('", ')
user = os.getenv("user").strip('", ')
password = os.getenv("password").strip('", ')
port = os.getenv("port").strip('", ')

# PostgreSQL 연결 설정
conn = psycopg2.connect(
    host=host,
    dbname=dbname,
    user=user,
    password=password,
    port=port
)

cur = conn.cursor()
cur.execute("SET client_encoding TO 'UTF8';")

# 작가 정보를 파싱하는 함수 (규칙: "이름 (역할) 아이디")
def parse_authors(author_string):
    authors_raw = author_string.strip().rstrip(',')
    authors = authors_raw.split('/') if '/' in authors_raw else [authors_raw]
    result = []
    for a in authors:
        a = a.strip()
        m = re.match(r"^(.*?)\s*\((.*?)\)\s*(\S+)$", a)
        if m:
            name = m.group(1).strip()
            role = m.group(2).strip()
            author_id_val = m.group(3).strip()
            result.append({"name": name, "role": role, "author_id": author_id_val})
    return result

def map_role(role_str):
    role_mapping = {"글": 0, "그림": 1, "원작": 2}
    return role_mapping.get(role_str.strip(), 0)

# age_limit 파싱 함수: 문자열을 정수로 변환
def parse_age_limit(age_limit_str):
    print(age_limit_str)
    mapping = {
        "전체연령가": 0,
        "12세 이용가": 12,
        "15세 이용가": 15,
        "18세 이용가": 19
    }
    return mapping.get(age_limit_str.strip(), 0)

def parse_serial_cycle(weekday_str):
    cycle_mapping = {
        "월": "0",
        "화": "1",
        "수": "2",
        "목": "3",
        "금": "4",
        "토": "5",
        "일": "6"
    }
    # 콤마로 구분된 경우 분리, 아니라면 단일값 리스트 생성
    days = [d.strip() for d in weekday_str.split(",")]
    
    # 각 항목에서 첫 글자(요일 부분)를 추출하여 매핑 수행
    mapped = [cycle_mapping.get(day[0], "9") for day in days]
    return mapped

def map_serial_status(status_str):
    cycle_mapping = {
        "연재": 0,
        "완결": 1
    }
    # 공백 제거 및 매핑
    return cycle_mapping.get(status_str.strip(), 0)

# 이미 삽입된 웹툰 확인 (content_id 기준)
def check_webtoon_exists(contentid):
    cur.execute("SELECT EXISTS(SELECT 1 FROM public.webtoon WHERE content_id = %s)", (contentid,))
    return cur.fetchone()[0]

# 제공된 정보만 사용하여 단일 테이블에 삽입하는 함수  
# 여기서는 CSV의 요일(weekday) 값을 바탕으로 serial_cycle 필드를 저장합니다.
def insert_webtoon_data(contentid, title, writers, age_limit_str, brief_text,
                        interest_count, total_episodes, status, weekday, hashtags):
    try:
        if not contentid or not title or not writers:
            print(f"contentId : {contentid}, title : {title}, writers : {writers}")
            print(f"ContentID {contentid} 필수 데이터 누락으로 건너뜀.")
            return

        if check_webtoon_exists(contentid):
            print(f"ContentID {contentid} 이미 존재. 삽입 건너뜀.")
            return

        try:
            total_eps = int(str(total_episodes).replace("총", "").replace("화", "").strip())
        except Exception as e:
            print(f"total_episodes 변환 오류 (contentid {contentid}): {e}")
            return

        # interest_count는 문자열 그대로 사용
        interest = str(interest_count).strip()

        hashtags_list = hashtags.split(' ')
        # 첫 번째 해시태그를 장르로 사용 (존재하지 않으면 None 처리)
        genre = hashtags_list[0].lstrip('#') if hashtags_list and hashtags_list[0] != '' else None

        # 작가 정보는 JSON으로 저장 (파싱 후 저장)
        parsed_authors = parse_authors(writers)
        authors_json = json.dumps(parsed_authors, ensure_ascii=False)

        # age_limit 파싱 (예: "전체연령가" -> 0, "12세 이용가" -> 12, 등)
        age_limit = parse_age_limit(age_limit_str)

        # 연재 요일 정보를 serial_cycle 필드로 저장 (정수 ordinal 값들, 콤마 구분)
        serial_cycle = parse_serial_cycle(weekday)

        # CSV에 제공된 컬럼만 사용하여 삽입  
        # (serial_cycle과 hashtags 컬럼 추가)
        insert_query = '''
        INSERT INTO public.webtoon (
            content_id, title, age_limit, total_episode_count,
            description, interest_count, serial_source, serial_status, serial_cycle, genre
        )
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
        RETURNING id;
        '''
        cur.execute(insert_query, (
            contentid, title, age_limit, total_eps,
            brief_text, interest, 0, map_serial_status(status), serial_cycle, genre
        ))
        webtoon_pk = cur.fetchone()[0]

        # 작가 정보 처리: 이미 존재하는 작가는 새로 삽입하지 않고 연결 테이블에만 추가
        for auth in parsed_authors:
            name = auth["name"]
            role = auth["role"]
            author_id_val = auth["author_id"]

            cur.execute("SELECT id FROM public.author WHERE author_id = %s", (author_id_val,))
            result = cur.fetchone()
            if result:
                author_pk = result[0]
            else:
                insert_author_query = '''
                    INSERT INTO public.author (name, author_id)
                    VALUES (%s, %s)
                    RETURNING id;
                '''
                cur.execute(insert_author_query, (name, author_id_val))
                author_pk = cur.fetchone()[0]

            insert_webtoon_author_query = '''
                INSERT INTO public.webtoon_author (author_id, webtoon_id, author_role)
                VALUES (%s, %s, %s)
                ON CONFLICT DO NOTHING;
            '''
            cur.execute(insert_webtoon_author_query, (author_pk, webtoon_pk, map_role(role)))
        
        print(f"ContentID {contentid} 삽입 성공.")
    except Exception as e:
        conn.rollback()
        print(f"ContentID {contentid} 삽입 오류: {e}")
        return
    else:
        conn.commit()

def process_csv_and_insert():
    csv_file_path = r'C:\Users\LSY\Desktop\webtoon\webtoon\crawling\crawling\result\naver_webtoon_daily_crawl_result.csv'
    df = pd.read_csv(csv_file_path, encoding='utf-8')
    total_rows = len(df)
    for index, row in df.iterrows():
        row_dict = row.to_dict()
        try:
            insert_webtoon_data(
                contentid=row_dict['titleId'],
                title=row_dict['title'],
                writers=row_dict['writers'],
                age_limit_str=row_dict['age_limit'],
                brief_text=row_dict['brief_text'],
                interest_count=row_dict['interest_count'],
                total_episodes=row_dict['total_episodes'],
                status=row_dict['status'],
                weekday=row_dict['weekday'],
                hashtags=row_dict['hashtags']
            )
            progress = ((index+1) / total_rows) * 100
            print(f"Progress: {index+1} : {progress:.2f}%")
        except Exception as e:
            print(f"Row {index+1} 처리 오류: {e}")
            continue
    conn.commit()

process_csv_and_insert()

cur.close()
conn.close()

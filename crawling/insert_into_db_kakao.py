import psycopg2
import json
import pandas as pd
import csv  # 추가: CSV 처리를 위한 모듈
import time
from psycopg2 import extras
import os
from dotenv import load_dotenv

load_dotenv()

host = os.getenv("host").strip('", ')
dbname = os.getenv("dbname").strip('", ')
user = os.getenv("user").strip('", ')
password = os.getenv("password").strip('", ')
port = os.getenv("port").strip('", ')

print("host:", host)
print("dbname:", dbname)
print("user:", user)
print("password:", password)
print("port:", port)

# PostgreSQL 연결 설정
conn = psycopg2.connect(
    host=host,
    dbname=dbname,
    user=user,
    password=password,
    port=port
)

# 커서 생성
cur = conn.cursor()
cur.execute("SET client_encoding TO 'UTF8';")

# 작가 정보를 JSON으로 변환하는 함수
def parse_authors(author_string):
    print(author_string)
    authors = author_string.split('/') if '/' in author_string else [author_string]  # '/'로 구분되지 않으면 전체 문자열을 리스트로 처리
    author_dict = {}
    for author in authors:
        if '(글)' in author:
            for writer in author.replace('(글)', '').split(','):
                author_dict[writer.strip()] = '글'
        elif '(그림)' in author:
            for illustrator in author.replace('(그림)', '').split(','):
                author_dict[illustrator.strip()] = '그림'
        elif '(원작)' in author:
            for original in author.replace('(원작)', '').split(','):
                author_dict[original.strip()] = '원작'
    return json.dumps(author_dict, ensure_ascii=False)

# 해시태그가 존재하는지 확인하고, 존재하지 않으면 새로 삽입한 후 webtoons에 id를 추가하는 함수
def update_or_insert_hashtags(webtoon_id, hashtags_list):
    for hashtag in hashtags_list:
        hashtag = hashtag.replace('#', '')  # 해시태그에서 '#' 제거
        cur.execute("SELECT id, webtoons FROM webtoon.hash_tag WHERE name = %s", (hashtag,))
        result = cur.fetchone()
        if result:
            hashtag_id, webtoons = result
            if webtoon_id not in webtoons:
                webtoons.append(webtoon_id)
                cur.execute(
                    "UPDATE webtoon.hash_tag SET webtoons = %s, updated_dt = NOW(), updated_id = 'system' WHERE id = %s", 
                    (webtoons, hashtag_id))  # psycopg2가 Python 리스트를 PostgreSQL 배열로 처리
        else:
            cur.execute(
                "INSERT INTO webtoon.hash_tag (name, webtoons, created_dt, created_id, updated_dt, updated_id) VALUES (%s, %s, NOW(), %s, NOW(), %s) RETURNING id",
                (hashtag, [webtoon_id], 'system', 'system'))  # 여기서도 Python 리스트로 저장

# cycle을 매핑하는 함수
def map_cycle(cycle_str):
    cycle_mapping = {
        "월": 0,
        "화": 1,
        "수": 2,
        "목": 3,
        "금": 4,
        "토": 5,
        "일": 6,
        "완결": 9
    }
    # 공백 제거 및 매핑
    return cycle_mapping.get(cycle_str.strip(), 9)  # 기본값은 9 (완결)

def map_role(role_str):
    cycle_mapping = {
        "글" : 0,
        "그림" : 1,
        "원작" : 2
    }
    # 공백 제거 및 매핑
    return cycle_mapping.get(role_str.strip(), 0)

def map_serial_status(status_str):
    cycle_mapping = {
        "연재": 0,
        "완결": 1
    }
    # 공백 제거 및 매핑
    return cycle_mapping.get(status_str.strip(), 0)

# 웹툰이 이미 존재하는지 확인하는 함수
def check_webtoon_exists(contentid):
    cur.execute("SELECT EXISTS(SELECT 1 FROM webtoon.webtoon WHERE id = %s)", (contentid,))
    return cur.fetchone()[0]

def update_or_insert_author(author_dict, webtoon_id):
    for author_name, role in author_dict.items():
        cur.execute("SELECT id, webtoon_id FROM webtoon.author WHERE name = %s", (author_name,))
        result = cur.fetchone()
        if result:
            author_id, webtoon_ids = result
            prefixed_webtoon_id = f"k_{webtoon_id}"
            if prefixed_webtoon_id not in webtoon_ids:
                webtoon_ids.append(prefixed_webtoon_id)
                cur.execute(
                    "UPDATE webtoon.author SET webtoon_id = %s, updated_dt = NOW(), updated_id = 'system' WHERE id = %s",
                    (webtoon_ids, author_id))
        else:
            prefixed_webtoon_id = f"k_{webtoon_id}"
            cur.execute(
                "INSERT INTO webtoon.author (name, webtoon_id, created_dt, created_id, updated_dt, updated_id) VALUES (%s, %s::bigint[], NOW(), %s, NOW(), %s) RETURNING id",
                (author_name, [int(prefixed_webtoon_id.split('_')[1])], 'system', 'system'))


# 웹툰 데이터를 삽입하는 함수
def insert_webtoon_data(contentid, title, author, total_episodes, genre_str, age_limit, view_count, comment_count, last_upload_day, serialization_status, cycle, badges, brief_text, hashtags):
    try:
        # 이미 존재하는지 확인
        #if check_webtoon_exists(contentid):
        #    print(f"ContentID {contentid} already exists. Skipping insertion.")
        #    return

        hashtags_list = hashtags.split(' ')  # 해시태그가 공백으로 구분되어 있다고 가정

        # cycle 매핑
        mapped_cycle = map_cycle(cycle)

        genre = genre_str.split('/')[1].strip()

        insert_query = '''
        INSERT INTO public.webtoon (
            content_id, title, total_episode_count, genre, age_limit,
            view_count, comment_count, last_upload_date, serial_status,
            serial_cycle, serial_source, description
        )
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
        ON CONFLICT (id) DO NOTHING
        RETURNING id
        ;
        '''

        cur.execute(insert_query, (
            contentid, title, total_episodes, genre, age_limit,
            view_count, comment_count, last_upload_day, map_serial_status(serialization_status),
            mapped_cycle, 1, brief_text
        ))
        
        webtoon_id = cur.fetchone()

        # 작가 정보 파싱
        author_json = parse_authors(author)
        author_dict = json.loads(author_json)
        for author_name, role in author_dict.items():
            insert_author_query = '''
                INSERT INTO public.author(
                    name
                )
                VALUES (%s)
                RETURNING id;
            '''
            insert_webtoon_author_query = '''
                INSERT INTO public.webtoon_author(
                    author_id, webtoon_id, author_role
                )
                VALUES (%s, %s, %s)
            '''
            cur.execute(insert_author_query, (author_name,))
            author_id = cur.fetchone()
            cur.execute(insert_webtoon_author_query, (author_id, webtoon_id, map_role(role)))

        #update_or_insert_hashtags(contentid, hashtags_list)
        #update_or_insert_author(author_dict, contentid)  # 작가 정보 업데이트 또는 삽입
        print(f"ContentID {contentid} inserted successfully.")
    
    except Exception as e:
        # 오류가 발생하면 트랜잭션을 롤백
        conn.rollback()
        print(f"Error inserting contentid {contentid}: {e}")
        return  # 예외가 발생한 경우 해당 트랜잭션은 종료하고 반환
    
    else:
        # 예외가 발생하지 않았다면 트랜잭션을 커밋
        conn.commit()

# CSV 파일을 읽고 한 줄씩 삽입하는 함수
def process_csv_and_insert():
    csv_file_path = r'C:\Users\LSY\Desktop\webtoon\webtoon\crawling\crawling\kakaopage_webtoons_detail_crawl_result.csv'
    df = pd.read_csv(csv_file_path, encoding='utf-8')

    total_rows = len(df)  # 총 행 수
    for index, row in df.iterrows():
        try:
            insert_webtoon_data(
                contentid=row['contentid'],
                title=row['title'],
                author=row['author'],
                total_episodes=row['total_episodes'],
                genre_str=row['genre'],
                age_limit=row['age_limit'],
                view_count=row['view_count'],
                comment_count=row['comment_count'],
                last_upload_day=row['last_upload_day'],
                serialization_status=row['serialization_status'],
                cycle=row['cycle'],
                badges=row['badges'],
                brief_text=row['brief_text'],
                hashtags=row['hashtags']
            )
            # contentid,title,author,total_episodes,genre,age_limit,view_count,comment_count,last_upload_day,serialization_status,cycle,badges,brief_text,hashtags
            # 진행 상황을 퍼센트로 출력
            progress = ((index + 1) / total_rows) * 100
            print(f"Progress: {index + 1} : {progress:.2f}%")  # 소수점 2자리까지 출력

        except Exception as e:
            # 데이터 삽입 중에 발생하는 모든 예외를 처리하고 해당 데이터 건너뛰기
            print(f"Error processing row {index + 1}: {e}")
            continue  # 오류 발생 시 해당 행을 건너뛰고 다음 행으로 진행

    conn.commit()

# CSV 파일 처리 및 데이터 삽입 실행
process_csv_and_insert()

# 커서 및 연결 종료
cur.close()
conn.close()
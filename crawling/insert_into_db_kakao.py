import psycopg2
import json
import pandas as pd
import csv  # 추가: CSV 처리를 위한 모듈
import time
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from webdriver_manager.chrome import ChromeDriverManager

# 셀레니움 설정 (필요한 경우만 유지)
chrome_options = Options()
chrome_options.add_argument("--window-size=1920,1080")
chrome_options.add_argument("--no-sandbox")
chrome_options.add_argument("--disable-dev-shm-usage")

# 웹드라이버 설치 및 실행 (필요한 경우만 유지)
driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=chrome_options)

# PostgreSQL 연결 설정
conn = psycopg2.connect(
    host="localhost",
    dbname="webtoon",
    user="postgres",
    password="a1234",
    port="5432"
)

# 커서 생성
cur = conn.cursor()
cur.execute("SET client_encoding TO 'UTF8';")

# 작가 정보를 JSON으로 변환하는 함수
def parse_authors(author_string):
    authors = author_string.split('/')
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
        cur.execute("SELECT id, webtoons FROM webtoon.hashtags WHERE name = %s", (hashtag,))
        result = cur.fetchone()
        if result:
            hashtag_id, webtoons = result
            if webtoon_id not in webtoons:
                webtoons.append(webtoon_id)
                cur.execute("UPDATE webtoon.hashtags SET webtoons = %s, updated_dt = NOW(), updated_id = 'system' WHERE id = %s", 
                            (webtoons, hashtag_id))
        else:
            cur.execute("INSERT INTO webtoon.hashtags (name, webtoons, created_dt, created_id, updated_dt, updated_id) VALUES (%s, %s, NOW(), %s, NOW(), %s) RETURNING id",
                        (hashtag, [webtoon_id], 'system', 'system'))

# cycle을 매핑하는 함수
def map_cycle(cycle_str):
    cycle_mapping = {
        "월": 1,
        "화": 2,
        "수": 3,
        "목": 4,
        "금": 5,
        "토": 6,
        "일": 7,
        "완결": 0
    }
    # 공백 제거 및 매핑
    return cycle_mapping.get(cycle_str.strip(), 0)  # 기본값은 0 (완결)

# 크롤링된 데이터를 저장할 함수
def insert_webtoon_data(contentid, title, author_string, total_episodes, age_limit, serialization_status, cycle, badges, brief_text, hashtags):
    author_json = parse_authors(author_string)
    hashtags_list = hashtags.split(' ')  # 해시태그가 공백으로 구분되어 있다고 가정

    # cycle 매핑
    mapped_cycle = map_cycle(cycle)

    insert_query = '''
    INSERT INTO webtoon.kakao_webtoon (id, title, author, total_episodes, status, upload_cycle, age_limit, biref_text, hashtags, created_dt, created_id, updated_dt, updated_id)
    VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, NOW(), %s, NOW(), %s)
    '''
    
    data = (
        contentid,
        title,
        author_json,
        total_episodes,
        serialization_status,
        mapped_cycle,  # 매핑된 cycle 값 사용
        age_limit,
        brief_text,
        json.dumps(hashtags_list),
        'system',
        'system'
    )

    cur.execute(insert_query, data)

    update_or_insert_hashtags(contentid, hashtags_list)

# CSV 파일을 읽고 한 줄씩 삽입하는 함수
def process_csv_and_insert():
    csv_file_path = './crawling/result/kakaopage_webtoons_finish_crawl_result.csv'  # 업데이트된 CSV 파일 경로로 변경
    df = pd.read_csv(csv_file_path, encoding='utf-8')

    total_rows = len(df)  # 총 행 수
    for index, row in df.iterrows():
        insert_webtoon_data(
            contentid=row['contentid'],
            title=row['title'],
            author_string=row['author'],
            total_episodes=row['total_episodes'],
            age_limit=row['age_limit'],
            serialization_status=row['serialization_status'],
            cycle=row['cycle'],  # 이미 매핑된 값인지 확인 필요
            badges=row['badges'],
            brief_text=row['brief_text'],
            hashtags=row['hashtags']
        )
        # 진행 상황을 퍼센트로 출력
        progress = ((index + 1) / total_rows) * 100
        print(f"Progress: {index + 1} : {progress:.2f}%")  # 소수점 2자리까지 출력

    conn.commit()

# CSV 파일 처리 및 데이터 삽입 실행
process_csv_and_insert()

# 커서 및 연결 종료
cur.close()
conn.close()

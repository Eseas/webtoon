import psycopg2
import json
import pandas as pd
import csv
import time
from psycopg2 import extras

# PostgreSQL connection settings
conn = psycopg2.connect(
    host="localhost",
    dbname="webtoon",
    user="postgres",
    password="a1234",
    port="5432"
)

# Create cursor
cur = conn.cursor()
cur.execute("SET client_encoding TO 'UTF8';")

# Function to parse author information and convert to JSON
def parse_authors(author_string):
    print(author_string)
    authors = author_string.split('/') if '/' in author_string else [author_string]
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

# Function to update or insert hashtags
def update_or_insert_hashtags(webtoon_id, hashtags_list):
    for hashtag in hashtags_list:
        hashtag = hashtag.replace('#', '')
        cur.execute("SELECT id, webtoons FROM webtoon.hashtags WHERE name = %s", (hashtag,))
        result = cur.fetchone()
        if result:
            hashtag_id, webtoons = result
            if webtoon_id not in webtoons:
                webtoons.append(webtoon_id)
                cur.execute(
                    "UPDATE webtoon.hashtags SET webtoons = %s, updated_dt = NOW(), updated_id = 'system' WHERE id = %s", 
                    (webtoons, hashtag_id))
        else:
            cur.execute(
                "INSERT INTO webtoon.hashtags (name, webtoons, created_dt, created_id, updated_dt, updated_id) VALUES (%s, %s, NOW(), %s, NOW(), %s) RETURNING id",
                (hashtag, [webtoon_id], 'system', 'system'))

# Function to check if the webtoon already exists
def check_webtoon_exists(contentid):
    cur.execute("SELECT EXISTS(SELECT 1 FROM webtoon.naver_webtoon WHERE id = %s)", (contentid,))
    return cur.fetchone()[0]

# Function to update or insert author data
def update_or_insert_author(author_dict, webtoon_id):
    for author_name, role in author_dict.items():
        cur.execute("SELECT id, webtoon_id FROM webtoon.author WHERE name = %s", (author_name,))
        result = cur.fetchone()
        if result:
            author_id, webtoon_ids = result
            prefixed_webtoon_id = f"n_{webtoon_id}"
            if prefixed_webtoon_id not in webtoon_ids:
                webtoon_ids.append(prefixed_webtoon_id)
                cur.execute(
                    "UPDATE webtoon.author SET webtoon_id = %s, updated_dt = NOW(), updated_id = 'system' WHERE id = %s",
                    (webtoon_ids, author_id))
        else:
            prefixed_webtoon_id = f"n_{webtoon_id}"
            cur.execute(
                "INSERT INTO webtoon.author (name, webtoon_id, created_dt, created_id, updated_dt, updated_id) VALUES (%s, %s::bigint[], NOW(), %s, NOW(), %s) RETURNING id",
                (author_name, [int(prefixed_webtoon_id.split('_')[1])], 'system', 'system'))

# Function to convert age_limit string to integer
def convert_age_limit(age_limit_str):
    age_mapping = {
        '전체이용가': 0,
        '12세 이용가': 12,
        '15세 이용가': 15,
        '18세 이용가': 19
    }
    return age_mapping.get(age_limit_str, 0)

# Function to insert webtoon data
def insert_webtoon_data(contentid, title, author_string, total_episodes, age_limit, serialization_status, brief_text, hashtags, interest_count):
    try:
        # Check if webtoon already exists
        if check_webtoon_exists(contentid):
            print(f"ContentID {contentid} already exists. Skipping insertion.")
            return

        # Parse author information
        author_json = parse_authors(author_string)
        author_dict = json.loads(author_json)
        hashtags_list = hashtags.split(' ')

        # Convert age_limit to integer
        age_limit_int = convert_age_limit(age_limit)

        insert_query = '''
        INSERT INTO webtoon.naver_webtoon (id, title, author, age_limit, total_episodes, brief_text, interest_count, status, hashtags, created_dt, created_id, updated_dt, updated_id)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, NOW(), %s, NOW(), %s)
        '''
        
        data = (
            contentid,
            title,
            author_json,
            age_limit_int,
            total_episodes,
            brief_text,
            interest_count,
            serialization_status,
            json.dumps(hashtags_list, ensure_ascii=False),
            'system',
            'system'
        )

        cur.execute(insert_query, data)

        update_or_insert_hashtags(contentid, hashtags_list)
        update_or_insert_author(author_dict, contentid)
        print(f"ContentID {contentid} inserted successfully.")
    
    except Exception as e:
        conn.rollback()
        print(f"Error inserting contentid {contentid}: {e}\nData: {data}")
        return
    else:
        conn.commit()

# Function to read CSV and insert data
def process_csv_and_insert():
    csv_file_path = './crawling/result/naver_webtoon_crawl_result_update.csv'
    df = pd.read_csv(csv_file_path, encoding='utf-8')

    total_rows = len(df)
    for index, row in df.iterrows():
        try:
            insert_webtoon_data(
                contentid=row['contentid'],
                title=row['title'],
                author_string=row['author'],
                total_episodes=int(row['total_episodes'].replace('총 ', '').replace('화', '').strip()),
                age_limit=row['age_limit'],
                serialization_status=row['serialization_status'],
                brief_text=row['brief_text'],
                hashtags=row['hashtags'],
                interest_count=int(row.get('interest_count', '0').replace(',', '').strip())
            )
            progress = ((index + 1) / total_rows) * 100
            print(f"Progress: {index + 1} : {progress:.2f}%")

        except Exception as e:
            print(f"Error processing row {index + 1}: {e}\nRow data: {row.to_dict()}")
            continue

    conn.commit()

# Run CSV processing and data insertion
process_csv_and_insert()

# Close cursor and connection
cur.close()
conn.close()
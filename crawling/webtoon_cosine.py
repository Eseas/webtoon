import psycopg2
import json
import pandas as pd
import csv  # 추가: CSV 처리를 위한 모듈
import time
from psycopg2 import extras
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import os
from dotenv import load_dotenv
import torch
from torch.utils.data import DataLoader, TensorDataset

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

# 커서 생성
cur = conn.cursor()
cur.execute("SET client_encoding TO 'UTF8';")


def load_webtoon_data():
    select_query = '''
        select * from public.webtoon;
    '''
    cur.execute(select_query,)
    rows = cur.fetchall()
    col_names = [desc[0] for desc in cur.description]

    df = pd.DataFrame(rows, columns=col_names)
    return df

def calculate_and_store_similarity():
    df = load_webtoon_data()

    # TF-IDF 벡터화 (장르 + 설명 사용)
    tfidf = TfidfVectorizer(stop_words="english")
    tfidf_matrix = tfidf.fit_transform(df["genre"] + " " + df["description"])

    # 모든 웹툰 간의 코사인 유사도 계산
    cosine_sim = cosine_similarity(tfidf_matrix, tfidf_matrix)

    # 유사도 데이터를 PostgreSQL에 저장하기 위해 변환
    similarity_data = []
    for i in range(len(df)):
        for j in range(len(df)):
            webtoon_id1 = int(df.loc[i, "id"])  # numpy.int64 → int 변환
            webtoon_id2 = int(df.loc[j, "id"])  # numpy.int64 → int 변환
            similarity = float(cosine_sim[i][j])  # numpy.float64 → float 변환
            
            print(f"id1: {webtoon_id1}, id2: {webtoon_id2}, cosine: {similarity}")
            similarity_data.append((webtoon_id1, webtoon_id2, similarity))

    # 데이터 삽입
    insert_query = '''
        INSERT INTO webtoon_similarity (webtoon_id1, webtoon_id2, similarity) VALUES %s
    '''
    extras.execute_values(cur, insert_query, similarity_data)
    conn.commit()

    print("모든 웹툰 간의 코사인 유사도 저장 완료!")

calculate_and_store_similarity()


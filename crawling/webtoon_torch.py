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
import torch.nn as nn
import torch.optim as optim
from sklearn.preprocessing import MinMaxScaler

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

def load_similarity_data():
    query = '''
        SELECT webtoon_id1, webtoon_id2, similarity FROM public.webtoon_similarity;
    '''
    df = pd.read_sql_query(query, conn)
    return df

# 데이터 로드
similarity_df = load_similarity_data()
print("데이터 로드 성공")

# MinMax 정규화 (웹툰 ID를 0~1 사이 값으로 변환)
scaler = MinMaxScaler()
similarity_df[['webtoon_id1', 'webtoon_id2']] = scaler.fit_transform(similarity_df[['webtoon_id1', 'webtoon_id2']])

# 데이터 변환 (웹툰 ID와 유사도를 Tensor로 변환)
X = torch.tensor(similarity_df[['webtoon_id1', 'webtoon_id2']].values, dtype=torch.float32)
y = torch.tensor(similarity_df['similarity'].values, dtype=torch.float32).view(-1, 1)

# 데이터로더 생성
dataset = TensorDataset(X, y)
dataloader = DataLoader(dataset, batch_size=16, shuffle=True)

# 추천 모델 정의
class WebtoonRecommender(nn.Module):
    def __init__(self):
        super(WebtoonRecommender, self).__init__()
        self.fc1 = nn.Linear(2, 16)  # 입력: (webtoon_id1, webtoon_id2)
        self.fc2 = nn.Linear(16, 8)
        self.fc3 = nn.Linear(8, 1)   # 출력: similarity score

    def forward(self, x):
        x = torch.relu(self.fc1(x))
        x = torch.relu(self.fc2(x))
        return self.fc3(x)

# 모델 생성
model = WebtoonRecommender()
criterion = nn.MSELoss()  # 손실 함수 (Mean Squared Error)
optimizer = optim.Adam(model.parameters(), lr=0.01)

num_epochs = 100
for epoch in range(num_epochs):
    total_loss = 0
    for batch_X, batch_y in dataloader:
        optimizer.zero_grad()
        predictions = model(batch_X)
        loss = criterion(predictions, batch_y)
        loss.backward()
        optimizer.step()
        total_loss += loss.item()
        print(total_loss)
    print(epoch, "is end")

    if epoch % 10 == 0:
        print(f"Epoch {epoch}, Loss: {total_loss:.4f}")

# 학습 완료된 모델 저장
torch.save(model.state_dict(), "webtoon_recommendation_model.pth")
print("모델 학습 완료 및 저장!")
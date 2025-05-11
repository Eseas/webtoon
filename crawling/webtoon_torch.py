import psycopg2
import pandas as pd
import numpy as np
from psycopg2 import extras
from sklearn.feature_extraction.text import TfidfVectorizer
import os
from dotenv import load_dotenv
import torch
from torch.utils.data import DataLoader, TensorDataset
import torch.nn as nn
import torch.optim as optim

# 환경 변수 로드
load_dotenv()
host = os.getenv("host").strip('", ')
dbname = os.getenv("dbname").strip('", ')
user = os.getenv("user").strip('", ')
password = os.getenv("password").strip('", ')
port = os.getenv("port").strip('", ')

# PostgreSQL 연결
conn = psycopg2.connect(
    host=host,
    dbname=dbname,
    user=user,
    password=password,
    port=port
)
cur = conn.cursor()
cur.execute("SET client_encoding TO 'UTF8';")

# ✅ 웹툰 데이터 로드 (view_count, comment_count 제외)
def load_webtoon_data():
    query = '''
        SELECT contentid, title, author, total_episodes, genre, age_limit, 
               last_upload_day, serialization_status, cycle, badges, 
               brief_text, hashtags
        FROM public.webtoon;
    '''
    cur.execute(query)
    rows = cur.fetchall()
    col_names = [desc[0] for desc in cur.description]
    df = pd.DataFrame(rows, columns=col_names)
    return df

# ✅ TF-IDF 벡터 생성
df = load_webtoon_data()
df.fillna("", inplace=True)  # 결측값 제거

# 텍스트 데이터 조합 (모든 컬럼 연결)
text_data = (
    df["title"].astype(str) + " " +
    df["author"].astype(str) + " " +
    df["genre"].astype(str) + " " +
    df["serialization_status"].astype(str) + " " +
    df["cycle"].astype(str) + " " +
    df["badges"].astype(str) + " " +
    df["brief_text"].astype(str) + " " +
    df["hashtags"].astype(str)
)

# TF-IDF 벡터화
tfidf = TfidfVectorizer(stop_words="english")
tfidf_matrix = tfidf.fit_transform(text_data)

# 웹툰 contentid → 벡터 매핑
webtoon_vectors = {
    int(df.loc[i, "contentid"]): tfidf_matrix[i].toarray()[0]
    for i in range(len(df))
}

# ✅ 유사도 데이터 로드
similarity_df = pd.read_sql_query("SELECT * FROM webtoon_similarity", conn)

# ✅ 학습용 입력/타겟 구성
X_data = []
y_data = []

for _, row in similarity_df.iterrows():
    id1 = int(row["webtoon_id1"])
    id2 = int(row["webtoon_id2"])
    sim = float(row["similarity"])

    if id1 in webtoon_vectors and id2 in webtoon_vectors:
        vec1 = webtoon_vectors[id1]
        vec2 = webtoon_vectors[id2]
        combined_vec = np.concatenate([vec1, vec2])
        X_data.append(combined_vec)
        y_data.append(sim)

# ✅ PyTorch Tensor로 변환
X_tensor = torch.tensor(X_data, dtype=torch.float32)
y_tensor = torch.tensor(y_data, dtype=torch.float32).view(-1, 1)

# ✅ DataLoader 구성
dataset = TensorDataset(X_tensor, y_tensor)
dataloader = DataLoader(dataset, batch_size=32, shuffle=True)

# ✅ 모델 정의
class ContentRecommender(nn.Module):
    def __init__(self, input_size):
        super().__init__()
        self.fc1 = nn.Linear(input_size, 256)
        self.fc2 = nn.Linear(256, 64)
        self.fc3 = nn.Linear(64, 1)

    def forward(self, x):
        x = torch.relu(self.fc1(x))
        x = torch.relu(self.fc2(x))
        return self.fc3(x)

input_size = X_tensor.shape[1]
model = ContentRecommender(input_size)
criterion = nn.MSELoss()
optimizer = optim.Adam(model.parameters(), lr=0.005)

# ✅ 학습 루프
for epoch in range(50):
    total_loss = 0
    for batch_X, batch_y in dataloader:
        optimizer.zero_grad()
        preds = model(batch_X)
        loss = criterion(preds, batch_y)
        loss.backward()
        optimizer.step()
        total_loss += loss.item()
    print(f"Epoch {epoch+1} | Loss: {total_loss:.4f}")

# ✅ 모델 저장
torch.save(model.state_dict(), "content_based_webtoon_model.pth")
print("모델 학습 완료 및 저장됨.")

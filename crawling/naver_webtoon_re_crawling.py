import pandas as pd
import os
import re
import time
import requests
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from webdriver_manager.chrome import ChromeDriverManager

# 크롬 웹드라이버 설정 (크롬 사용 가정)
chrome_options = Options()
chrome_options.add_argument("--window-size=1920,1080")
driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=chrome_options)

# 네이버 로그인 페이지로 이동 (수동 로그인)
driver.get("https://nid.naver.com/nidlogin.login")
print("로그인을 완료하세요...")

while True:
    time.sleep(5)  # 5초씩 대기하면서 로그인 여부 확인
    if driver.current_url != "https://nid.naver.com/nidlogin.login":
        print("로그인 성공! 크롤링 작업을 시작합니다.")
        break
    else:
        print("로그인 대기 중...")

# 결과 CSV 파일이 위치한 폴더 경로
result_folder_path = os.path.join(os.getcwd(), 'crawling', 'result')
if not os.path.exists(result_folder_path):
    os.makedirs(result_folder_path)

# 이미 처리한 titleId를 추적 (결과 CSV 파일에서 로드)
processed_ids = set()
for filename in os.listdir(result_folder_path):
    # 파일 이름에 "naver_webtoon_daily_crawl_result"가 포함된 모든 csv 파일을 대상으로 함
    if filename.endswith('.csv') and "naver_webtoon_daily_crawl_result" in filename:
        file_path = os.path.join(result_folder_path, filename)
        try:
            df = pd.read_csv(file_path, dtype={'titleId': str})
            if 'titleId' in df.columns:
                processed_ids.update(df['titleId'].dropna().astype(str).tolist())
        except Exception as e:
            print(f"파일 {filename} 불러오기 실패: {e}")

print(f"초기 불러온 처리된 titleId 개수: {len(processed_ids)}")

# CSV 파일들을 찾는 함수 (원본 파일의 이름은 그대로 사용)
def find_webtoon_csv_files():
    csv_folder = os.path.join(os.getcwd(), 'crawling', 'result')
    # "naver_webtoon_daily_crawl_result"가 파일 이름에 포함된 csv 파일 모두를 읽음
    return [os.path.join(csv_folder, f) for f in os.listdir(csv_folder)
            if f.endswith('.csv') and "naver_webtoon_daily_crawl_result" in f]

# 웹툰 정보를 바로 CSV에 저장하는 함수 (파일 이름 변경 없이 그대로 저장)
def save_record_to_csv(record, csv_file_path):
    # 파일 이름 그대로 사용
    csv_filename = os.path.basename(csv_file_path)
    webtoon_info_csv_path = os.path.join(result_folder_path, csv_filename)
    
    # 파일이 이미 존재하는지 체크해서 처음 저장하는 경우 헤더 포함
    first_record = not os.path.exists(webtoon_info_csv_path)
    record_df = pd.DataFrame([record])
    record_df.to_csv(webtoon_info_csv_path, mode='a', index=False, header=first_record, encoding='utf-8')
    print(f"웹툰 정보 저장됨: {record}")

# 단일 웹툰 정보를 크롤링하는 함수
def crawl_single_webtoon_info(title_id):
    url = f"https://comic.naver.com/webtoon/list?titleId={title_id}"
    driver.get(url)
    time.sleep(5)  # 페이지 로딩 대기
    
    # 웹툰 제목
    try:
        title = driver.find_element(By.XPATH, "//*[contains(@class, 'EpisodeListInfo__title')]").text
    except Exception:
        title = "정보 없음"
    
    # 작가 정보
    try:
        writer_elements = driver.find_elements(By.XPATH, "//span[contains(@class, 'ContentMetaInfo__category--WwrCp')]")
        writers = []
        for writer_element in writer_elements:
            a_tags = writer_element.find_elements(By.XPATH, ".//a[contains(@class, 'ContentMetaInfo__link--xTtO6') and string-length(normalize-space(text())) > 0]")
            if not a_tags:
                continue
            a_tag = a_tags[0]
            href = a_tag.get_attribute("href")
            writer_name = a_tag.get_attribute("textContent").strip()
            writer_id = ""
            m = re.search(r'/artistTitle\?id=(\d+)', href)
            if m:
                writer_id = m.group(1)
            else:
                m2 = re.search(r'community/u/([^?]+)', href)
                if m2:
                    writer_id = m2.group(1)
            full_text = writer_element.get_attribute("textContent").strip()
            role = full_text.replace(writer_name, "", 1).replace("∙", "").strip()
            writers.append(f"{writer_name} ({role}) {writer_id}")
        writer = " / ".join(writers)
    except Exception:
        writer = "정보 없음"
    
    # 연재 주기와 이용 등급
    try:
        info_item = driver.find_element(By.XPATH, "//*[contains(@class, 'ContentMetaInfo__info_item')]").text
        info_parts = info_item.split('∙')
        weekday = info_parts[0].strip()
        age_rating = info_parts[1].strip()
    except Exception:
        weekday = "정보 없음"
        age_rating = "정보 없음"
    
    # 관심 수
    try:
        interest_count = driver.find_element(By.XPATH, "//*[contains(@class, 'EpisodeListUser__count')]").text
    except Exception:
        interest_count = "정보 없음"
    
    # 총 화수
    try:
        total_episodes = driver.find_element(By.XPATH, "//*[contains(@class, 'EpisodeListView__count')]").text
    except Exception:
        total_episodes = "정보 없음"
    
    # 간략한 소개글 (해시태그 제거)
    try:
        brief_text = driver.find_element(By.XPATH, "//*[contains(@class, 'EpisodeListInfo__summary')]").text
        brief_text = re.sub(r'#\S+', '', brief_text)
        brief_text = brief_text.replace('\n', '').replace('접기', '')
    except Exception:
        brief_text = "정보 없음"
    
    # 휴재 및 완결 여부 확인
    if "\n휴재" in title:
        title = title.replace("\"", '').replace("\n휴재", "").strip()
        status = "휴재"
    elif "완결" in weekday:
        total_episodes = total_episodes.replace("완결", "").strip()
        status = "완결"
    else:
        status = "연재 중"
    
    # 해시태그
    try:
        tags_element = driver.find_element(By.XPATH, "//*[contains(@class, 'TagGroup__tag_group')]")
        hashtags = tags_element.find_elements(By.XPATH, ".//*[contains(@class, 'TagGroup__tag')]")
        hashtag_str = ""
        for tag in hashtags:
            hashtag_text = tag.text.strip()
            hashtag_str += f" {hashtag_text}"
        hashtag_str = hashtag_str.strip()
    except Exception:
        hashtag_str = "정보 없음"
    
    record = {
        'titleId': title_id,
        'title': title,
        'writers': writer,
        'weekday': weekday,
        'age_rating': age_rating,
        'brief_text': brief_text,
        'interest_count': interest_count,
        'total_episodes': total_episodes,
        'status': status,
        'hashtags': hashtag_str
    }
    return record

# CSV 파일 내 "정보 없음"이 포함된 행을 재크롤링하여 업데이트하는 함수
def recrawl_missing_data(csv_file_path):
    print(f"재크롤링 진행: {csv_file_path}")
    df = pd.read_csv(csv_file_path, dtype={'titleId': str})
    updated = False
    # 체크할 컬럼 (titleId 제외)
    columns_to_check = ['title', 'writers', 'weekday', 'age_rating', 'brief_text',
                        'interest_count', 'total_episodes', 'status', 'hashtags']
    
    for idx, row in df.iterrows():
        # 지정 컬럼 중 하나라도 "정보 없음"이면 재크롤링
        if any(row[col] == "정보 없음" for col in columns_to_check):
            title_id = str(row['titleId']).strip()
            print(f"재크롤링: titleId {title_id} (index {idx})")
            new_record = crawl_single_webtoon_info(title_id)
            for col in columns_to_check:
                df.at[idx, col] = new_record[col]
            updated = True
            
    if updated:
        df.to_csv(csv_file_path, index=False, encoding='utf-8')
        print(f"파일 업데이트 완료: {csv_file_path}")
    else:
        print("모든 데이터가 완전합니다. 재크롤링 필요 없음.")

# 전체 CSV 파일에 대해 "정보 없음" 항목만 재크롤링 진행
csv_files = find_webtoon_csv_files()
for csv_file in csv_files:
    print(f"파일 {csv_file} 에 대한 재크롤링을 시작합니다 (정보 없음 확인).")
    recrawl_missing_data(csv_file)

driver.quit()
print("크롤링 완료 및 CSV 저장 완료!")
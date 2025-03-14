import pandas as pd
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import time
import os
import re
import requests
from webdriver_manager.chrome import ChromeDriverManager

# CSV 파일 경로 설정 (crawling 폴더 내의 파일 경로로 수정)
csv_file_path = os.path.join(os.getcwd(), 'crawling', 'naver_webtoon_daily_base_info.csv')
webtoon_data = pd.read_csv(csv_file_path)

# 웹드라이버 설정 (크롬 사용 가정)
chrome_options = Options()
chrome_options.add_argument("--window-size=1920,1080")
driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=chrome_options)

# 네이버 로그인 페이지로 이동
driver.get("https://nid.naver.com/nidlogin.login")

# 로그인 완료 여부를 확인하기 위해 특정 요소가 로드될 때까지 대기
print("로그인을 완료하세요...")

while True:
    time.sleep(5)  # 5초씩 대기하면서 로그인 여부 확인
    if driver.current_url != "https://nid.naver.com/nidlogin.login":  # 로그인 성공 시 URL이 바뀜
        print("로그인 성공! 크롤링 작업을 시작합니다.")
        break
    else:
        print("로그인 대기 중...")

# 이미지 저장 기본 경로
base_image_path = r'C:\Users\LSY\Desktop\webtoon\webtoon\webtoon\src\main\resources\static\naver'

# 크롤링 결과 저장할 리스트
webtoon_info_list = []
hashtag_set = set()  # 해시태그 정보를 중복 없이 저장할 set

# 총 웹툰 수
total_webtoons = len(webtoon_data)
headers = {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.102 Safari/537.36"
    }

for index, row in webtoon_data.iterrows():
    title_id = row['titleId']

    # 웹툰 페이지로 이동
    url = f"https://comic.naver.com/webtoon/list?titleId={title_id}"
    driver.get(url)

    time.sleep(2)  # 페이지 로드 시간 대기

    # 웹툰 제목 가져오기
    try:
        title = driver.find_element(By.XPATH, "//*[contains(@class, 'EpisodeListInfo__title')]").text
    except:
        title = "정보 없음"

    # 작가 정보 가져오기 (여러 작가 처리 및 역할 구분)
    try:
        writer_elements = driver.find_elements(By.XPATH, "//*[contains(@class, 'ContentMetaInfo__category')]")
        writers = []
        for writer_element in writer_elements:
            author_info = writer_element.text
            author_name, author_role = author_info.split('∙')
            author_name = author_name.strip().replace("\n", "")
            author_role = author_role.strip().replace("\n", "")
            writers.append(f"{author_name} ({author_role})")
        writer = '/ '.join(writers)
    except:
        writer = "정보 없음"

    # 연재 주기와 이용 등급 가져오기
    try:
        info_item = driver.find_element(By.XPATH, "//*[contains(@class, 'ContentMetaInfo__info_item')]").text
        info_parts = info_item.split('∙')
        weekday = info_parts[0].strip()  # 연재 주기
        age_rating = info_parts[1].strip()  # 이용 등급
    except:
        weekday = "정보 없음"
        age_rating = "정보 없음"

    # 관심 수 가져오기
    try:
        interest_count = driver.find_element(By.XPATH, "//*[contains(@class, 'EpisodeListUser__count')]").text
    except:
        interest_count = "정보 없음"

    # 총 화수 가져오기
    try:
        total_episodes = driver.find_element(By.XPATH, "//*[contains(@class, 'EpisodeListView__count')]").text
    except:
        total_episodes = "정보 없음"

    try:
        brief_text = driver.find_element(By.XPATH, "//*[contains(@class, 'EpisodeListInfo__summary')]").text
        # 해시태그(# 뒤에 나오는 문자들) 제거
        brief_text = re.sub(r'#\S+', '', brief_text)
        # 줄바꿈 문자를 공백으로 대체
        brief_text = brief_text.replace('\n', '').replace('접기', '')
    except:
        brief_text = "정보 없음"

    # 휴재 및 완결 여부 확인
    if "\n휴재" in title:
        title = title.replace("\"", '').replace("\n휴재", "").strip()  # title에서 휴재와 줄바꿈 제거
        status = "휴재"
    elif "완결" in weekday:
        total_episodes = total_episodes.replace("완결", "").strip()  # total_episodes에서 완결 제거
        status = "완결"
    else:
        status = "연재 중"
    
    # 이미지 링크 가져오기    
    try:
        image_element = WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.XPATH, "//*[contains(@class, 'Poster__image')]"))
        )
        image_url = image_element.get_attribute('src')
    except:
        image_url = None

    # 이미지 다운로드 경로 설정
    if image_url:
        # 각 연재 주기와 titleId에 맞는 폴더 경로 생성
        folder_path = os.path.join(base_image_path, weekday, str(title_id))

        # 폴더가 존재하지 않으면 생성
        if not os.path.exists(folder_path):
            os.makedirs(folder_path)

        # 이미지 파일 경로 설정
        image_filename = os.path.join(folder_path, f'{title_id}.jpg')

        # 이미지 다운로드
        image_response = requests.get(image_url, headers=headers)
        with open(image_filename, 'wb') as file:
            file.write(image_response.content)
    
    # 해시태그 가져오기
    try:
        tags_element = driver.find_element(By.XPATH, "//*[contains(@class, 'TagGroup__tag_group')]")
        hashtags = tags_element.find_elements(By.XPATH, ".//*[contains(@class, 'TagGroup__tag')]")

        hashtag_str = ""  # 하나의 문자열로 저장할 해시태그
        for tag in hashtags:
            hashtag_text = tag.text.strip()
            hashtag_id = tag.get_attribute('href').split('id=')[-1]  # id 추출

            # 중복 없이 해시태그 저장 (set 사용)
            hashtag_set.add((hashtag_id, hashtag_text))

            hashtag_str += f" {hashtag_text}"  # 공백으로 구분하여 추가

        hashtag_str = hashtag_str.strip()  # 앞뒤 공백 제거
    except:
        hashtag_str = "정보 없음"

    # 웹툰 정보 저장 (이미지 경로는 저장하지 않음)
    webtoon_info_list.append({
        'titleId': title_id,
        'title': title,
        'writers': writer,
        'weekday': weekday,
        'age_rating': age_rating,
        'brief_text': brief_text,
        'interest_count': interest_count,
        'total_episodes': total_episodes,
        'status': status,  # 상태 추가 (휴재, 완결, 연재 중)
        'hashtags': hashtag_str  # 해시태그 문자열 추가
    })

    # 진행 상황 출력
    print(f"현재 웹툰 데이터: {webtoon_info_list[-1]}")
    progress = ((index + 1) / total_webtoons) * 100
    print(f"작업 진행 중: {index + 1}/{total_webtoons} 완료 ({progress:.2f}%)")

# 결과를 데이터프레임으로 변환 (웹툰 기본 정보)
webtoon_info_df = pd.DataFrame(webtoon_info_list)

# 결과 CSV 파일을 crawling/result 폴더에 저장
result_folder_path = os.path.join(os.getcwd(), 'crawling', 'result')

# 폴더가 존재하지 않으면 생성
if not os.path.exists(result_folder_path):
    os.makedirs(result_folder_path)

webtoon_info_csv_path = os.path.join(result_folder_path, 'naver_webtoon_crawl_result.csv')

webtoon_info_df.to_csv(webtoon_info_csv_path, index=False, encoding='utf-8')

# 해시태그 중복 제거 후 CSV 저장
hashtag_csv_path = os.path.join(result_folder_path, 'finish_naver_hashtag.csv')

hashtag_df = pd.DataFrame(list(hashtag_set), columns=['hashtag_id', 'hashtag_text'])
hashtag_df.to_csv(hashtag_csv_path, index=False, encoding='utf-8')

# 브라우저 종료
driver.quit()

print("크롤링 완료 및 CSV 저장 완료!")

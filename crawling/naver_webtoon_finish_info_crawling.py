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

# 크롬 웹드라이버 설정 (크롬 사용 가정)
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

# 크롤링 결과 저장할 리스트와 처리한 titleId 추적을 위한 set
hashtag_set = set()  # 해시태그 정보를 중복 없이 저장할 set
processed_ids = set()  # 이미 처리한 titleId 저장

result_folder_path = os.path.join(os.getcwd(), 'crawling', 'result')
if os.path.exists(result_folder_path):
    for filename in os.listdir(result_folder_path):
        if filename.endswith('.csv'):
            file_path = os.path.join(result_folder_path, filename)
            try:
                df = pd.read_csv(file_path, dtype={'titleId': str})  # titleId를 문자열로 변환하여 로드
                if 'titleId' in df.columns:
                    processed_ids.update(df['titleId'].dropna().astype(str).tolist())  # NaN 제거 후 문자열 변환
            except Exception as e:
                print(f"파일 {filename} 불러오기 실패: {e}")

print(f"초기 불러온 처리된 titleId 개수: {len(processed_ids)}")

# 크롤링할 CSV 파일을 찾는 함수
def find_webtoon_csv_files():
    csv_folder = os.path.join(os.getcwd(), 'crawling')
    return [os.path.join(csv_folder, f) for f in os.listdir(csv_folder) if f.startswith('naver_webtoon_daily_base_info') and f.endswith('.csv')]

# 각 웹툰 정보를 바로 CSV에 저장하는 함수
def save_record_to_csv(record, csv_file_path):
    # 결과 파일 경로: crawling/result 폴더 안에 저장
    result_folder_path = os.path.join(os.getcwd(), 'crawling', 'result')
    if not os.path.exists(result_folder_path):
        os.makedirs(result_folder_path)
    csv_filename = os.path.basename(csv_file_path).replace("base_info", "crawl_result")
    webtoon_info_csv_path = os.path.join(result_folder_path, csv_filename)
    
    # 파일이 이미 존재하는지 체크해서 처음 저장하는 경우 헤더 포함
    first_record = not os.path.exists(webtoon_info_csv_path)
    
    record_df = pd.DataFrame([record])
    record_df.to_csv(webtoon_info_csv_path, mode='a', index=False, header=first_record, encoding='utf-8')
    print(f"웹툰 정보 저장됨: {record}")

# 크롤링 함수
def crawl_webtoon_info(csv_file_path):
    webtoon_data = pd.read_csv(csv_file_path)
    total_webtoons = len(webtoon_data)
    headers = {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.102 Safari/537.36"
    }

    for index, row in webtoon_data.iterrows():
        title_id = str(row['titleId']).strip()  # titleId를 문자열(str)로 변환하여 비교

        # titleId 중복 체크: 이미 처리된 경우 건너뛰기
        if title_id in processed_ids:
            print(f"이미 처리된 titleId {title_id} 건너뜀.")
            continue
        else:
            print(f"새로운 id {title_id} 추가")
            processed_ids.add(title_id)  # 중복 방지를 위해 집합에 추가
        # 웹툰 페이지로 이동
        url = f"https://comic.naver.com/webtoon/list?titleId={title_id}"
        driver.get(url)
        time.sleep(5)  # 페이지 로드 시간 대기

        # 웹툰 제목 가져오기
        try:
            title = driver.find_element(By.XPATH, "//*[contains(@class, 'EpisodeListInfo__title')]").text
        except Exception as e:
            title = "정보 없음"

        try:
            writer_elements = driver.find_elements(By.XPATH, "//span[contains(@class, 'ContentMetaInfo__category--WwrCp')]")
            writers = []
            for writer_element in writer_elements:
                # 텍스트가 있는 a 태그만 선택 (작가 이름이 있는 a 태그)
                a_tags = writer_element.find_elements(By.XPATH, ".//a[contains(@class, 'ContentMetaInfo__link--xTtO6') and string-length(normalize-space(text())) > 0]")
                if not a_tags:
                    continue
                a_tag = a_tags[0]
                href = a_tag.get_attribute("href")
                writer_name = a_tag.get_attribute("textContent").strip()

                # 두 가지 패턴 모두 처리: 
                # 1. /artistTitle?id=숫자
                # 2. community/u/ID값 (쿼리 스트링 전까지)
                writer_id = ""
                m = re.search(r'/artistTitle\?id=(\d+)', href)
                if m:
                    writer_id = m.group(1)
                else:
                    m2 = re.search(r'community/u/([^?]+)', href)
                    if m2:
                        writer_id = m2.group(1)

                # writer_element의 전체 텍스트에서 a 태그의 텍스트(작가명)를 제거한 후 남은 부분을 역할(role)로 사용
                full_text = writer_element.get_attribute("textContent").strip()
                role = full_text.replace(writer_name, "", 1).replace("∙", "").strip()

                writers.append(f"{writer_name} ({role}) {writer_id}")

            writer = " / ".join(writers)
        except Exception as e:
            writer = "정보 없음"

        # 연재 주기와 이용 등급 가져오기
        try:
            info_item = driver.find_element(By.XPATH, "//*[contains(@class, 'ContentMetaInfo__info_item')]").text
            info_parts = info_item.split('∙')
            weekday = info_parts[0].strip()  # 연재 주기
            age_rating = info_parts[1].strip()  # 이용 등급
        except Exception as e:
            weekday = "정보 없음"
            age_rating = "정보 없음"

        # 관심 수 가져오기
        try:
            interest_count = driver.find_element(By.XPATH, "//*[contains(@class, 'EpisodeListUser__count')]").text
        except Exception as e:
            interest_count = "정보 없음"

        # 총 화수 가져오기
        try:
            total_episodes = driver.find_element(By.XPATH, "//*[contains(@class, 'EpisodeListView__count')]").text
        except Exception as e:
            total_episodes = "정보 없음"

        # 간략한 소개글 가져오기 및 해시태그 제거
        try:
            brief_text = driver.find_element(By.XPATH, "//*[contains(@class, 'EpisodeListInfo__summary')]").text
            brief_text = re.sub(r'#\S+', '', brief_text)  # 해시태그 제거
            brief_text = brief_text.replace('\n', '').replace('접기', '')
        except Exception as e:
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
        '''
        # 이미지 링크 가져오기
        try:
            image_element = WebDriverWait(driver, 10).until(
                EC.presence_of_element_located((By.XPATH, "//*[contains(@class, 'Poster__image')]"))
            )
            image_url = image_element.get_attribute('src')
        except Exception as e:
            image_url = None

        # 이미지 다운로드
        if image_url:
            folder_path = os.path.join(base_image_path, str(status), str(title_id))
            if not os.path.exists(folder_path):
                os.makedirs(folder_path)
            image_filename = os.path.join(folder_path, f'{title_id}.jpg')
            image_response = requests.get(image_url, headers=headers)
            if image_response.status_code == 200:
                with open(image_filename, 'wb') as file:
                    file.write(image_response.content)
                if os.path.exists(image_filename):
                    print(f"이미지 다운로드 성공: {image_filename}")
                else:
                    print("이미지 다운로드 실패: 파일이 생성되지 않았습니다.")
            else:
                print(f"이미지 다운로드 실패: HTTP 응답 코드 {image_response.status_code}")
        '''
        # 해시태그 가져오기
        try:
            tags_element = driver.find_element(By.XPATH, "//*[contains(@class, 'TagGroup__tag_group')]")
            hashtags = tags_element.find_elements(By.XPATH, ".//*[contains(@class, 'TagGroup__tag')]")
            hashtag_str = ""
            for tag in hashtags:
                hashtag_text = tag.text.strip()
                hashtag_id = tag.get_attribute('href').split('id=')[-1]
                hashtag_set.add((hashtag_id, hashtag_text))
                hashtag_str += f" {hashtag_text}"
            hashtag_str = hashtag_str.strip()
        except Exception as e:
            hashtag_str = "정보 없음"

        # 웹툰 정보 저장을 위한 레코드 생성
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
        
        # 각 웹툰 정보를 바로 CSV 파일에 저장
        save_record_to_csv(record, csv_file_path)

        # 진행 상황 출력
        progress = ((index + 1) / total_webtoons) * 100
        print(f"작업 진행 중: {index + 1}/{total_webtoons} 완료 ({progress:.2f}%)")

# 전체 CSV 파일을 순차적으로 크롤링
csv_files = find_webtoon_csv_files()
for csv_file in csv_files:
    print(f"파일 {csv_file} 에 대한 크롤링을 시작합니다.")
    crawl_webtoon_info(csv_file)

# 해시태그 중복 제거 후 CSV 저장
hashtag_csv_path = os.path.join(os.getcwd(), 'crawling', 'result', 'finish_naver_hashtag.csv')
hashtag_df = pd.DataFrame(list(hashtag_set), columns=['hashtag_id', 'hashtag_text'])
hashtag_df.to_csv(hashtag_csv_path, index=False, encoding='utf-8')
print(f"해시태그 정보가 {hashtag_csv_path} 에 저장되었습니다.")

# 브라우저 종료
driver.quit()
print("크롤링 완료 및 CSV 저장 완료!")

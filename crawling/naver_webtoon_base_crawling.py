import os
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from webdriver_manager.chrome import ChromeDriverManager
import time
import csv

# Selenium 설정
options = webdriver.ChromeOptions()
# options.headless = True  # 브라우저를 띄우지 않고 수행 (headless 모드를 끔)
driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)

# 요일과 dailyPlus 페이지 URL 리스트
tabs = ["mon", "tue", "wed", "thu", "fri", "sat", "sun", "dailyPlus"]

webtoon_data = []

# 요일 매핑 함수
def map_day(tab_value):
    day_mapping = {
        "mon": 1,
        "tue": 2,
        "wed": 3,
        "thu": 4,
        "fri": 5,
        "sat": 6,
        "sun": 7,
        "dailyPlus": 8  # dailyPlus는 8로 매핑
    }
    return day_mapping.get(tab_value, 0)  # 기본값은 0 (해당 없음)

# 각 페이지에서 데이터 수집
for tab in tabs:
    # 네이버 웹툰 페이지 접속
    url = f'https://comic.naver.com/webtoon?tab={tab}'
    driver.get(url)

    # 웹툰 리스트 요소가 로드될 때까지 기다림
    try:
        WebDriverWait(driver, 30).until(
            EC.visibility_of_element_located((By.CSS_SELECTOR, '[class*="ContentList__content_list"]'))
        )
    except Exception as e:
        print(f"페이지 로드 중 오류 발생: {e}")
        continue

    # 웹툰 데이터 추출
    webtoons = driver.find_elements(By.CSS_SELECTOR, '[class*="ContentList__info_area"]')

    # 데이터 수집
    for webtoon in webtoons:
        try:
            title_element = webtoon.find_element(By.CSS_SELECTOR, 'span[class*="ContentTitle__title"] .text')
            title = title_element.text
            link_element = webtoon.find_element(By.CSS_SELECTOR, 'a[class*="ContentTitle__title_area"]')
            href = link_element.get_attribute('href')
            title_id = href.split('titleId=')[1].split('&')[0]
            day = map_day(tab)
            upload_day = tab  # upload_day 추가
            webtoon_data.append([title_id, title, day, upload_day])
            print(f"수집된 데이터 - titleId: {title_id}, Title: {title}, Day: {day}, Upload Day: {upload_day}")
        except Exception as e:
            print(f"데이터 수집 중 오류 발생: {e}")
            continue

# CSV 파일 저장 경로
csv_base_dir = os.getcwd()  # 현재 작업 경로 가져오기
csv_folder = os.path.join(csv_base_dir, 'crawling')
os.makedirs(csv_folder, exist_ok=True)  # crawling 폴더 생성

# CSV 파일로 저장
csv_filename = 'naver_webtoon_daily_base_info.csv'
csv_filepath = os.path.join(csv_folder, csv_filename)

with open(csv_filepath, 'w', newline='', encoding='utf-8-sig') as file:
    writer = csv.writer(file)
    writer.writerow(['titleId', 'Title', 'Day', 'Upload Day'])
    writer.writerows(webtoon_data)

# 드라이버 종료
driver.quit()

print("크롤링 완료, 데이터가 CSV 파일에 저장되었습니다.")
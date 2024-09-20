import os
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from webdriver_manager.chrome import ChromeDriverManager
import time
import csv

# Selenium 설정
options = webdriver.ChromeOptions()
options.headless = True  # 브라우저를 띄우지 않고 수행
driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)

# 네이버 웹툰 페이지 접속
driver.get('https://comic.naver.com/webtoon?tab=dailyPlus')

# 페이지 로드를 기다림
time.sleep(5)  # 적절한 로딩 시간 설정

# 웹툰 데이터 추출
webtoons = driver.find_elements(By.CSS_SELECTOR, '.DailyListItem__item--LP6_T')
#webtoons = driver.find_elements(By.CSS_SELECTOR, '.ContentList__content_list--q5KXY')

#webtoons = driver.find_element(By.XPATH, "//*[contains(@class, 'ContentList__content_list--q5KXY')]")

webtoon_data = []

# 데이터 수집
for webtoon in webtoons:
    title_id = webtoon.find_element(By.CSS_SELECTOR, 'a.Poster__link--sopnC').get_attribute('href').split('titleId=')[1].split('&')[0]
    title = webtoon.find_element(By.CSS_SELECTOR, '.text').text
    webtoon_data.append([title_id, title])

# CSV 파일 저장 경로
csv_base_dir = os.getcwd()  # 현재 작업 경로 가져오기
csv_folder = os.path.join(csv_base_dir, 'crawling')
os.makedirs(csv_folder, exist_ok=True)  # crawling 폴더 생성

# CSV 파일로 저장
csv_filename = 'naver_webtoon_daily_base_info.csv'
csv_filepath = os.path.join(csv_folder, csv_filename)

with open(csv_filepath, 'w', newline='', encoding='utf-8-sig') as file:
    writer = csv.writer(file)
    writer.writerow(['titleId', 'Title'])
    writer.writerows(webtoon_data)

# 드라이버 종료
driver.quit()

print("크롤링 완료, 데이터가 CSV 파일에 저장되었습니다.")

import time
import csv
import os
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from webdriver_manager.chrome import ChromeDriverManager

# 셀레니움 설정
chrome_options = Options()
chrome_options.add_argument("--window-size=1920,1080")
chrome_options.add_argument("--no-sandbox")
chrome_options.add_argument("--disable-dev-shm-usage")

# 웹드라이버 설치 및 실행
driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=chrome_options)

# 저장할 폴더 경로 설정
output_dir = os.path.join(os.getcwd(), 'crawling', 'result')
os.makedirs(output_dir, exist_ok=True)  # 폴더가 없으면 생성

# 결과를 저장할 CSV 파일 경로 설정
csv_file = os.path.join(output_dir, "kakaopage_webtoons.csv")

with open(csv_file, mode="w", newline='', encoding='utf-8') as file:
    writer = csv.writer(file)
    writer.writerow(["contentId"])

    # 각 tab_uid 페이지에 대해 크롤링 수행
    for tab_uid in list(range(1, 8)) + [12]:  # 1 ~ 7 + 12번 페이지
        url = f"https://page.kakao.com/menu/10010/screen/52?tab_uid={tab_uid}"
        driver.get(url)
        time.sleep(2)

        # 무한 스크롤을 위해 페이지 끝까지 스크롤
        last_height = driver.execute_script("return document.body.scrollHeight")
        while True:
            driver.execute_script("window.scrollTo(0, document.body.scrollHeight);")
            time.sleep(2)
            new_height = driver.execute_script("return document.body.scrollHeight")
            if new_height == last_height:
                break
            last_height = new_height

        # 웹툰 항목들 가져오기
        items = driver.find_elements(By.CSS_SELECTOR, "a[href^='/content/']")

        for item in items:
            # href 속성에서 contentId 추출
            content_url = item.get_attribute("href")
            content_id = content_url.split("/content/")[1]

            # 결과를 CSV 파일에 작성
            writer.writerow([content_id])

# 드라이버 종료
driver.quit()

print(f"크롤링 완료. {csv_file}에 저장되었습니다.")
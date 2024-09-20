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

# 카카오 페이지 로그인 URL
login_url = "https://page.kakao.com/login"

# 카카오 페이지 로그인 페이지로 이동
driver.get(login_url)
print("카카오페이지 로그인 페이지에 접속했습니다. 로그인 후 완료될 때까지 기다립니다.")

# 로그인 확인: 로그인 완료될 때까지 대기
while True:
    try:
        # 로그인 후 확인 가능한 요소 (예: 로그인 후 나타나는 프로필 이미지)
        driver.find_element(By.CSS_SELECTOR, "img[class^='profile-image']")
        print("로그인 완료. 크롤링을 시작합니다.")
        break  # 로그인 성공하면 루프 종료
    except:
        # 로그인 완료 전까지 대기
        print("로그인이 아직 완료되지 않았습니다. 5초 후 다시 확인합니다.")
        time.sleep(5)  # 5초 대기 후 다시 확인

# 저장할 폴더 경로 설정
output_dir = os.path.join(os.getcwd(), 'crawling', 'result')
os.makedirs(output_dir, exist_ok=True)  # 폴더가 없으면 생성

# base 정보가 저장된 CSV 파일 경로
webtoons = os.path.join(output_dir, "kakaopage_webtoons_base_info.csv")

# 결과를 저장할 CSV 파일 경로
detail_info_file = os.path.join(output_dir, "kakaopage_webtoons_detail_info.csv")

# base 정보 파일에서 contentId 목록 불러오기
content_ids = []

with open(webtoons, mode="r", newline='', encoding='utf-8') as file:
    reader = csv.reader(file)
    next(reader)  # 첫 번째 헤더 건너뛰기
    for row in reader:
        content_ids.append(row[0])  # contentId는 첫 번째 열에 있음
    total_webtoons = len(content_ids)

# 상세 정보를 저장할 CSV 파일 열기
with open(detail_info_file, mode="w", newline='', encoding='utf-8') as file:
    writer = csv.writer(file)

    # 각 contentId에 대해 크롤링 수행
    for content_id in content_ids:
        url = f"https://page.kakao.com/content/{content_id}"
        driver.get(url)
        time.sleep(2)  # 페이지가 로드될 때까지 대기
        print(f"접속한 페이지 URL: {url}")

        try:
            title = driver.find_element(By.CSS_SELECTOR, "span.font-large3-bold.mb-3pxr.text-ellipsis.break-all.text-el-70.line-clamp-2").text
        except Exception as e:
            title = '정보없음'
        
        # 작가 추출
        try:
            author = driver.find_element(By.CSS_SELECTOR, "span.font-small2.mb-6pxr.text-ellipsis.text-el-70.opacity-70.break-word-anywhere.line-clamp-2").text
            # 쉼표를 슬래시로 변환
            author = author.replace(",", "/")
        except Exception as e:
            author = '정보없음'
        


# 드라이버 종료
driver.quit()

print(f"크롤링 완료. {detail_info_file}에 저장되었습니다.")

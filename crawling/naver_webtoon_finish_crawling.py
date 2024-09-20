from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from bs4 import BeautifulSoup
import time
import csv
import os

# 크롬 웹드라이버 경로 설정
chrome_driver_path = '/path/to/chromedriver'  # chromedriver의 경로로 변경하세요
chrome_service = Service(chrome_driver_path)

# 크롬 옵션 설정
chrome_options = Options()
chrome_options.add_argument('--headless')  # 브라우저 창을 열지 않고 실행
chrome_options.add_argument('--no-sandbox')
chrome_options.add_argument('--disable-dev-shm-usage')

# 웹드라이버 시작
driver = webdriver.Chrome(service=chrome_service, options=chrome_options)
driver.get('https://comic.naver.com/webtoon?tab=finish')

# 페이지 끝까지 스크롤하는 함수
def scroll_to_bottom(driver):
    last_height = driver.execute_script("return document.body.scrollHeight")
    while True:
        # 페이지 끝까지 스크롤
        driver.execute_script("window.scrollTo(0, document.body.scrollHeight);")
        time.sleep(2)  # 페이지가 로딩되는 시간을 기다림

        # 새로운 높이를 확인
        new_height = driver.execute_script("return document.body.scrollHeight")
        if new_height == last_height:
            break
        last_height = new_height

# 페이지 스크롤을 통해 모든 데이터 로드
scroll_to_bottom(driver)

# 페이지 소스를 가져와 BeautifulSoup으로 파싱
soup = BeautifulSoup(driver.page_source, 'html.parser')

# 데이터 추출
webtoons = []
items = soup.find_all('a', class_='Poster__link--sopnC')

for item in items:
    href = item['href']  # href 속성 추출
    title_id = href.split('titleId=')[1]  # titleId 추출
    title_tag = item.find_next('span', class_='text')  # 제목 추출
    title = title_tag.get_text(strip=True) if title_tag else 'Unknown'  # 제목이 없을 경우 대비

    webtoons.append({
        'titleId': title_id,
        'title': title
    })

# 크롬 드라이버 종료
driver.quit()

# CSV 파일 저장 경로
csv_base_dir = os.getcwd()  # 현재 작업 경로 가져오기
csv_folder = os.path.join(csv_base_dir, 'crawling')
os.makedirs(csv_folder, exist_ok=True)  # crawling 폴더 생성

# 500개씩 나눠서 CSV 파일로 저장
batch_size = 500  # 한 파일에 저장할 항목 개수
total_batches = (len(webtoons) + batch_size - 1) // batch_size  # 총 파일 개수 계산

for i in range(total_batches):
    start_index = i * batch_size
    end_index = start_index + batch_size
    batch_webtoons = webtoons[start_index:end_index]

    # 파일 이름에 인덱스를 붙여서 구분
    csv_filename = f'naver_webtoon_base_info-{i + 1}.csv'
    csv_filepath = os.path.join(csv_folder, csv_filename)

    with open(csv_filepath, mode='w', newline='', encoding='utf-8') as file:
        writer = csv.DictWriter(file, fieldnames=['titleId', 'title'])
        writer.writeheader()
        writer.writerows(batch_webtoons)

    print(f'{csv_filepath} 파일에 저장되었습니다.')

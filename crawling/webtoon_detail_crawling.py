import time
import csv
import os
import re
import requests
from requests.exceptions import ChunkedEncodingError
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.support.ui import WebDriverWait
from selenium.common.exceptions import WebDriverException
from selenium.webdriver.support import expected_conditions as EC
from webdriver_manager.chrome import ChromeDriverManager

# 셀레니움 설정
chrome_options = Options()
chrome_options.add_argument("--window-size=1920,1080")
chrome_options.add_argument("--no-sandbox")
chrome_options.add_argument("--disable-dev-shm-usage")

# 웹드라이버 설치 및 실행
driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=chrome_options)

# 카카오 페이지 로그인 URL
login_url = "https://accounts.kakao.com/login/?continue=https%3A%2F%2Fkauth.kakao.com%2Foauth%2Fauthorize%3Fis_popup%3Dfalse%26ka%3Dsdk%252F1.38.0%2520os%252Fjavascript%2520lang%252Fko-KR%2520device%252FWin32%2520origin%252Fhttps%25253A%25252F%25252Fpage.kakao.com%26auth_tran_id%3Drh8p018b7m49bbb48c5fdb0199e5da1b89de359484m1ebyco3%26response_type%3Dcode%26state%3Dhttps%25253A%25252F%25252Fpage.kakao.com%25252Fmenu%25252F10010%25252Fscreen%25252F52%26redirect_uri%3Dhttps%253A%252F%252Fpage.kakao.com%252Frelay%252Flogin%26through_account%3Dtrue%26client_id%3D49bbb48c5fdb0199e5da1b89de359484&talk_login=hidden#login"

# 카카오 페이지 로그인 페이지로 이동
driver.get(login_url)
print("카카오페이지 로그인 페이지에 접속했습니다. 로그인 후 완료될 때까지 기다립니다.")

# 로그인 확인: 아래 화살표 이미지가 나타날 때까지 대기
while True:
    try:
        arrow_icon = driver.find_element(By.CSS_SELECTOR, "img[alt='아래 화살표'][width='12'][height='12']")
        if arrow_icon:
            print("로그인 완료. 크롤링을 시작합니다.")
            break
    except:
        print("로그인이 아직 완료되지 않았습니다. 5초 후 다시 확인합니다.")
        time.sleep(5)

# 저장할 폴더 경로 설정
output_dir = os.path.join(os.getcwd(), 'crawling', 'result')
os.makedirs(output_dir, exist_ok=True)

# 결과를 저장할 CSV 파일 경로
detail_info_file = os.path.join(output_dir, "kakaopage_webtoons_finish_crawl_result_1.csv")

file_exists = os.path.isfile(detail_info_file)

# base 정보 파일에서 contentId 목록 불러오기
content_ids = []
age_limits = []

webtoons = os.path.join(output_dir, "kakaopage_webtoons_base_info.csv")
with open(webtoons, mode="r", newline='', encoding='utf-8') as file:
    reader = csv.reader(file)
    next(reader)
    for row in reader:
        content_ids.append(row[0])
        if row[1] == '15세':
            age_limits.append(15)
        elif row[1] == '19세':
            age_limits.append(19)
        else:
            age_limits.append(0)
    total_webtoons = len(content_ids)

base_image_path = os.path.join(os.getcwd(), 'webtoon', 'src', 'main', 'resources', 'static', 'kakao_main_image')

# 일정 크롤링 후 브라우저를 새로 고침
REFRESH_LIMIT = 100  # 100개마다 새로고침

# 이미지 다운로드 함수
def download_image(image_url, image_filename, max_retries=3):
    for attempt in range(max_retries):
        try:
            image_response = requests.get(image_url, timeout=10)  # 타임아웃 설정
            if image_response.status_code == 200:
                with open(image_filename, 'wb') as file:
                    file.write(image_response.content)
                print(f"이미지 다운로드 성공: {image_filename}")
                return True
            else:
                print(f"이미지 다운로드 실패: HTTP 응답 코드 {image_response.status_code}")
        except ChunkedEncodingError:
            print(f"이미지 다운로드 실패: ChunkedEncodingError, 재시도 중... ({attempt + 1}/{max_retries})")
        except Exception as e:
            print(f"이미지 다운로드 오류 발생: {e}, 재시도 중... ({attempt + 1}/{max_retries})")
        time.sleep(2)  # 재시도 전 잠깐 대기
    return False

# 상세 정보를 저장할 CSV 파일 열기
with open(detail_info_file, mode="a", newline='', encoding='utf-8') as file:
    writer = csv.writer(file)

    # 파일이 없을 때만 헤더를 추가
    if not file_exists:
        writer.writerow(["contentid", "title", "author", "total_episodes", "age_limit", "serialization_status", "cycle", "badges", "brief_text", "hashtags"])

    # 각 contentId에 대해 크롤링 수행
    #for index, content_id in enumerate(content_ids):
    #for index, content_id in enumerate(content_ids[1042:], start=1043):
    #for index, content_id in enumerate(content_ids[1538:], start=1539):
    #for index, content_id in enumerate(content_ids[1652:], start=1653):
    for index, content_id in enumerate(content_ids[:1042], start=0):
        try:
            url = f"https://page.kakao.com/content/{content_id}"
            driver.get(url)
            time.sleep(2)

            try:
                title = driver.find_element(By.CSS_SELECTOR, "span.font-large3-bold.mb-3pxr.text-ellipsis.break-all.text-el-70.line-clamp-2").text
            except:
                title = '정보없음'
    
            # 연재주기 추출 및 저장
            try:
                serialization_cycle = driver.find_element(By.CSS_SELECTOR, "div.mt-6pxr.flex.items-center span.font-small2.text-el-70.opacity-70").text
                if "완결" in serialization_cycle:
                    serialization_status = "완결"
                    cycle = "완결"
                else:
                    serialization_status = "연재"
                    cycle = serialization_cycle.replace(" 연재", "")
            except:
                serialization_status = "정보없음"
                cycle = "정보없음"
    
            # 총 편수 추출
            try:
                total_episodes_text = driver.find_element(By.CSS_SELECTOR, "span.text-ellipsis.break-all.line-clamp-1.font-small2-bold.text-el-70").text
                total_episodes = int(total_episodes_text.replace("전체 ", "").strip())
            except:
                total_episodes = '정보없음'
    
            # 뱃지 구분: 3다무, 기다무 여부 확인
            badges = []
            try:
                if driver.find_element(By.CSS_SELECTOR, "img[alt='3다무 뱃지']"):
                    badges.append("3다무")
            except:
                pass
            try:
                if driver.find_element(By.CSS_SELECTOR, "img[alt='기다무 뱃지']"):
                    badges.append("기다무")
            except:
                pass
            badges_str = "/".join(badges) if badges else "없음"
    
            # 추가 정보 URL로 이동 (tab_type=about)
            additional_info_url = f"https://page.kakao.com/content/{content_id}?tab_type=about"
            driver.get(additional_info_url)
            time.sleep(2)
    
            # brief_text 추출 및 줄바꿈 문자 제거
            try:
                brief_text = driver.find_element(By.CSS_SELECTOR, "span.font-small1.mb-8pxr.block.whitespace-pre-wrap.break-words.text-el-70").text
                brief_text = brief_text.replace('\n', '<br>')
            except:
                brief_text = '정보없음'
    
            # 해시태그 추출 및 이어붙이기
            try:
                hashtags_elements = driver.find_elements(By.CSS_SELECTOR, "span.font-small2-bold.text-ellipsis.text-el-70.line-clamp-1")
                hashtags = " ".join([element.text for element in hashtags_elements if "기다무 대여권" not in element.text])
            except:
                hashtags = '정보없음'
    
            # about 페이지에서 작가 정보 추출
            try:
                writer_name = driver.find_element(By.XPATH, "//span[text()='글']/following-sibling::span").text
                artist = driver.find_element(By.XPATH, "//span[text()='그림']/following-sibling::span").text
                original_author = None
                try:
                    original_author = driver.find_element(By.XPATH, "//span[text()='원작']/following-sibling::span").text
                except:
                    pass
                if original_author:
                    author = f"{writer_name} (글)/ {artist} (그림)/ {original_author} (원작)"
                else:
                    author = f"{writer_name} (글)/ {artist} (그림)"
            except:
                author = '정보없음'
    
            # 해당 웹툰의 연령 제한 가져오기
            age_limit = age_limits[index]
            
            '''
            try:
                image_element = WebDriverWait(driver, 10).until(
                    EC.presence_of_element_located((By.CSS_SELECTOR, "img[alt='썸네일']"))
                )
                image_url = image_element.get_attribute('src')
                if image_url.startswith("//"):  # URL이 "//"로 시작하면 "https:"를 추가
                    image_url = "https:" + image_url
            except:
                image_url = None
            
            # 이미지 다운로드 부분
            if image_url:
                # 폴더 생성
                folder_path = os.path.join(base_image_path, content_id)
                if not os.path.exists(folder_path):
                    os.makedirs(folder_path)
    
                image_filename = os.path.join(folder_path, f'{content_id}.jpg')
    
                # 이미지 다운로드 시도
                if not download_image(image_url, image_filename):
                    print(f"이미지 다운로드를 여러 번 시도했으나 실패: {image_filename}")
            else:
                print(f"이미지 URL을 찾을 수 없습니다: {content_id}")
            '''
            # CSV 파일에 저장
            writer.writerow([content_id, title, author, total_episodes, age_limit, serialization_status, cycle, badges_str, brief_text, hashtags])
    
            # 진행 상황 퍼센트로 출력
            progress_percentage = (index + 1) / 1042 * 100
            print(f"진행 상황: {progress_percentage:.2f}% 완료 ({index + 1}/{1042})")
        except WebDriverException as e:
            print(f"WebDriverException 발생. 재시도 중... {content_id}, 오류: {e}")
            driver.quit()  # 브라우저 종료
            driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=chrome_options)  # 브라우저 재시작
            continue  # 현재 항목 건너뛰고 다음 항목으로 진행

# 드라이버 종료
driver.quit()

print(f"크롤링 완료. {detail_info_file}에 저장되었습니다.")

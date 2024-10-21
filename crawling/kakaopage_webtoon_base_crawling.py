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
login_url = "https://accounts.kakao.com/login/?continue=https%3A%2F%2Fkauth.kakao.com%2Foauth%2Fauthorize%3Fis_popup%3Dfalse%26ka%3Dsdk%252F1.38.0%2520os%252Fjavascript%2520lang%252Fko-KR%2520device%252FWin32%2520origin%252Fhttps%25253A%25252F%25252Fpage.kakao.com%26auth_tran_id%3Drh8p018b7m49bbb48c5fdb0199e5da1b89de359484m1ebyco3%26response_type%3Dcode%26state%3Dhttps%25253A%25252F%25252Fpage.kakao.com%25252Fmenu%25252F10010%25252Fscreen%25252F52%26redirect_uri%3Dhttps%253A%252F%252Fpage.kakao.com%252Frelay%252Flogin%26through_account%3Dtrue%26client_id%3D49bbb48c5fdb0199e5da1b89de359484&talk_login=hidden#login"

# 카카오 페이지 로그인 페이지로 이동
driver.get(login_url)
print("카카오페이지 로그인 페이지에 접속했습니다. 로그인 후 완료될 때까지 기다립니다.")

# 로그인 확인: 아래 화살표 이미지가 나타날 때까지 대기
while True:
    try:
        # 아래 화살표 이미지 확인
        arrow_icon = driver.find_element(By.CSS_SELECTOR, "img[alt='아래 화살표'][width='12'][height='12']")
        if arrow_icon:
            print("로그인 완료. 크롤링을 시작합니다.")
            break  # 로그인 성공하면 루프 종료
    except:
        # 로그인 완료 전까지 대기
        print("로그인이 아직 완료되지 않았습니다. 5초 후 다시 확인합니다.")
        time.sleep(5)  # 5초 대기 후 다시 확인


# 저장할 폴더 경로 설정
output_dir = os.path.join(os.getcwd(), 'crawling')
os.makedirs(output_dir, exist_ok=True)  # 폴더가 없으면 생성

# 결과를 저장할 CSV 파일 경로 설정
csv_file = os.path.join(output_dir, "kakaopage_webtoons_base_info.csv")

with open(csv_file, mode="a", newline='', encoding='utf-8') as file:
    writer = csv.writer(file)
    writer.writerow(["contentId", "age"])

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

            # 해당 웹툰의 나이 뱃지 확인
            try:
                badge = item.find_element(By.XPATH, ".//div[@class='absolute right-4pxr top-4pxr']/img")
                badge_alt = badge.get_attribute("alt")
                print(badge_alt)

                if "19세" in badge_alt:
                    age = "19세"
                elif "15세" in badge_alt:
                    age = "15세"
                else:
                    age = "all"
            except:
                # 뱃지가 없는 경우 'all'로 설정
                print("찾지 못함")
                age = "all"

            # 결과를 CSV 파일에 작성
            writer.writerow([content_id, age])

# 드라이버 종료
driver.quit()

print(f"크롤링 완료. {csv_file}에 저장되었습니다.")

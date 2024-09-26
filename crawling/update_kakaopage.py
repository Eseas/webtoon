import csv
import time
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from webdriver_manager.chrome import ChromeDriverManager

# 셀레니움 설정
chrome_options = Options()
chrome_options.add_argument("--headless")  # 브라우저를 띄우지 않고 실행
chrome_options.add_argument("--window-size=1920,1080")
chrome_options.add_argument("--no-sandbox")
chrome_options.add_argument("--disable-dev-shm-usage")

# 웹드라이버 설치 및 실행
driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=chrome_options)

# 기존 CSV 파일 경로
csv_file_path = './crawling/result/kakaopage_webtoons_finish_crawl_result.csv'

# 업데이트된 CSV 파일 경로 (새로운 파일로 저장)
updated_csv_file_path = './crawling/result/kakaopage_webtoons_finish_crawl_result_updated.csv'

# cycle을 매핑하는 함수
def map_cycle(cycle_str):
    cycle_mapping = {
        "월": 1,
        "화": 2,
        "수": 3,
        "목": 4,
        "금": 5,
        "토": 6,
        "일": 7,
        "완결": 0
    }
    # 공백 제거 및 매핑
    return cycle_mapping.get(cycle_str.strip(), 0)  # 기본값은 0 (완결)

# 작가 정보를 재크롤링하는 함수
def re_crawl_author(contentid):
    # 여기에 실제 작가 정보를 크롤링할 URL 패턴을 넣어주세요.
    # 예: "https://example.com/webtoon/{contentid}/about"
    about_page_url = f"https://kakao.com/webtoon/{contentid}/about"  # 예시 URL

    try:
        driver.get(about_page_url)
        time.sleep(2)  # 페이지 로딩 대기

        # 글 작가 추출
        try:
            writer_name = driver.find_element(By.XPATH, "//span[text()='글']/following-sibling::span").text
        except:
            writer_name = '정보없음'

        # 그림 작가 추출
        try:
            artist = driver.find_element(By.XPATH, "//span[text()='그림']/following-sibling::span").text
        except:
            artist = '정보없음'

        # 원작 작가 추출
        try:
            original_author = driver.find_element(By.XPATH, "//span[text()='원작']/following-sibling::span").text
        except:
            original_author = None  # 원작 작가는 존재하지 않을 수 있으므로 None 처리

        # 작가 정보를 적절히 포맷팅하고 쉼표 제거
        if original_author:
            author = f"{writer_name} (글) / {artist} (그림) / {original_author} (원작)"
        else:
            author = f"{writer_name} (글) / {artist} (그림)"
        
        author = author.replace(',', '')  # 쉼표 제거
        return author

    except Exception as e:
        print(f"Error re-crawling author for contentid {contentid}: {e}")
        return '정보없음'

# CSV 파일 읽기 및 수정
updated_rows = []
with open(csv_file_path, mode='r', newline='', encoding='utf-8') as file:
    reader = csv.reader(file)
    header = next(reader)  # 헤더 읽기
    updated_rows.append(header)  # 헤더는 그대로 유지

    # 'brief_text'와 기타 컬럼의 인덱스를 찾습니다.
    try:
        brief_text_index = header.index('brief_text')
        hashtags_index = header.index('hashtags')  # 'hashtags' 컬럼 인덱스도 찾기
        author_index = header.index('author')  # 'author' 컬럼 인덱스
        total_episodes_index = header.index('total_episodes')  # 'total_episodes' 컬럼 인덱스
        cycle_index = header.index('cycle')  # 'cycle' 컬럼 인덱스
    except ValueError as e:
        print(f"Error: {e}")
        driver.quit()
        exit()

    for row_num, row in enumerate(reader, start=2):  # 헤더 다음부터 시작
        # 필드 수가 10개 미만인 경우 오류 메시지 출력 후 건너뛰기
        if len(row) < 10:
            print(f"Line {row_num}: Expected at least 10 fields, but got {len(row)}. Skipping this line.")
            continue

        # 필드 수가 10개를 초과하는 경우 처리
        if len(row) > 10:
            # 0부터 brief_text_index-1까지는 그대로 유지
            fixed_row = row[:brief_text_index]

            # brief_text_index부터 마지막 전까지를 하나의 문자열로 합치고 쉼표 제거
            brief_text_parts = row[brief_text_index:-1]
            combined_brief_text = ''.join(brief_text_parts).replace(',', '')
            fixed_row.append(combined_brief_text)

            # 마지막 필드는 'hashtags'
            hashtags = row[-1]
            fixed_row.append(hashtags)
        else:
            # 필드 수가 정확한 경우 그대로 사용
            fixed_row = row

        # 'brief_text' 수정: 줄바꿈 문자를 <br>로 변경하고 쉼표 제거
        original_brief_text = fixed_row[brief_text_index].replace('\n', '<br>').replace(',', '')
        fixed_row[brief_text_index] = original_brief_text

        # 'cycle' 매핑
        original_cycle = fixed_row[cycle_index]
        if original_cycle.isdigit() and int(original_cycle) > 1000:
            fixed_row[cycle_index] = '정보없음'
        else:
            fixed_row[cycle_index] = str(map_cycle(original_cycle))

        # 'total_episodes'가 1000을 초과하면 '정보없음'으로 설정하고 쉼표 제거
        original_total_episodes = fixed_row[total_episodes_index]
        try:
            if int(original_total_episodes) > 1000:
                fixed_row[total_episodes_index] = '정보없음'
            else:
                # 쉼표 제거
                fixed_row[total_episodes_index] = original_total_episodes.replace(',', '')
        except ValueError:
            # 숫자가 아닌 경우 '정보없음'으로 설정
            fixed_row[total_episodes_index] = '정보없음'

        # 'hashtags'가 비어있는 경우 '정보없음'으로 설정
        hashtags = fixed_row[hashtags_index].strip()
        if not hashtags:
            fixed_row[hashtags_index] = '정보없음'

        # 'author'가 '정보없음'인 경우 재크롤링하여 정보 업데이트
        author = fixed_row[author_index].strip()
        if author == '정보없음':
            contentid = fixed_row[0]  # 'contentid'는 첫 번째 컬럼이라고 가정
            print(f"Line {row_num}: Re-crawling author for contentid {contentid}...")
            updated_author = re_crawl_author(contentid)
            fixed_row[author_index] = updated_author

        # 업데이트된 row 추가
        updated_rows.append(fixed_row)

# 새로운 CSV 파일에 저장
with open(updated_csv_file_path, mode='w', newline='', encoding='utf-8') as file:
    writer = csv.writer(file)
    writer.writerows(updated_rows)

# 드라이버 종료
driver.quit()

print(f"CSV 파일 업데이트 완료. {updated_csv_file_path}에 저장되었습니다.")

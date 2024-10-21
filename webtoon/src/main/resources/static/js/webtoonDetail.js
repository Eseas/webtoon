// 데이터 요청을 위한 AJAX 코드
function requestWebtoonData() {
    // URL에서 id와 platform 값 가져오기
    const urlParams = new URLSearchParams(window.location.search);
    const id = urlParams.get('id');
    const platform = urlParams.get('platform');

    // platform 값 확인
    if (platform !== 'naver' && platform !== 'kakao') {
        console.error('Invalid platform value. Use either "naver" or "kakao".');
        return;
    }

    // AJAX 요청
    const xhr = new XMLHttpRequest();
    xhr.open('GET', `/api/webtoon?id=${id}&platform=${platform}`, true);

    // 요청 완료 시 실행될 콜백 함수
    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
                // 성공적으로 데이터를 받았을 때 처리
                const response = JSON.parse(xhr.responseText);
                console.log('Response:', response);

                // 웹툰 정보 화면에 표시
                displayWebtoonData(response, platform);
            } else {
                // 오류 발생 시 처리
                console.error('Error:', xhr.status, xhr.statusText);
            }
        }
    };

    // 요청 전송
    xhr.send();
}

// 웹툰 정보를 화면에 표시하는 함수
function displayWebtoonData(data, platform) {
    const container = document.getElementById('webtoon-container');
    container.innerHTML = '';

    // 데이터가 존재할 경우 요소 업데이트
    if (data) {
        // 플랫폼에 따른 웹툰 링크 설정
        let webtoonLink = '#';
        if (platform === 'naver') {
            webtoonLink = `https://comic.naver.com/webtoon/list?titleId=${data.id}`;
        } else if (platform === 'kakao') {
            webtoonLink = `https://page.kakao.com/content/${data.id}`;
        }

        container.innerHTML = `
            <img src="/static/kakao_main_image/${data.id}/${data.id}.jpg" alt="${data.title || '웹툰 메인 이미지'}">
            <div class="divider"></div>
            <div class="webtoon-info">
                <a href="${webtoonLink}" target="_blank">
                    <button>해당 웹툰 보러 가기</button>
                </a>
                <h2>${data.title || '제목 없음'}</h2>
                <p>작가: ${data.author ? Object.values(data.author).join(', ') : '알 수 없음'}</p>
                <p>총 에피소드: ${data.totalEpisodes || '알 수 없음'}</p>
                <p>상태: ${data.status || '알 수 없음'}</p>
                ${data.status !== '완결' ? `<p>업로드 주기: ${getUploadCycleText(data.uploadCycle)}</p>` : ''}
                <p>연령 제한: ${getAgeLimitText(data.ageLimit)}</p>
                <p>${data.briefText || '설명 없음'}</p>
                <p class="hashtags">해시태그: ${data.hashtags || '없음'}</p>
            </div>
        `;
    }
}

// 연령 제한 텍스트 변환 함수
function getAgeLimitText(ageLimit) {
    if (ageLimit === 0) {
        return '전체 이용가';
    } else if (ageLimit === 15) {
        return '15세 이용가';
    } else if (ageLimit === 18) {
        return '19세 이용가';
    } else {
        return '알 수 없음';
    }
}

// 업로드 주기 텍스트 변환 함수
function getUploadCycleText(uploadCycle) {
    const days = ['월요일', '화요일', '수요일', '목요일', '금요일', '토요일', '일요일'];
    if (Number.isInteger(uploadCycle)) {
        const uploadDays = uploadCycle.toString().split('').map(num => days[parseInt(num) - 1]).join(', ');
        return uploadDays;
    } else {
        return '알 수 없음';
    }
}

// 페이지가 열리면 함수 실행
document.addEventListener('DOMContentLoaded', requestWebtoonData);

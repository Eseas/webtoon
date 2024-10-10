/* 초기 웹툰 데이터 로딩 */
document.addEventListener('DOMContentLoaded', function() {
    loadInitialWebtoons();
});

/* 웹툰 리스트 DOM 요소 */
const webtoonList = document.getElementById('webtoon-list');

/* 초기 웹툰 데이터 로딩 함수 (AJAX 사용) - 40개 로드 */
function loadInitialWebtoons() {
    const xhr = new XMLHttpRequest();
    xhr.open('GET', '/api/webtoons?limit=40', true);
    xhr.onload = function() {
        if (xhr.status === 200) {
            const webtoons = JSON.parse(xhr.responseText);
            webtoons.forEach(addWebtoonItem);
        } else {
            console.error('웹툰 데이터를 불러오는데 실패했습니다.');
        }
    };
    xhr.onerror = function() {
        console.error('웹툰 데이터를 불러오는 중 오류가 발생했습니다.');
    };
    xhr.send();
}

/* 추가 웹툰 데이터 로딩 함수 (AJAX 사용) - 20개 추가 로드 */
function loadMoreWebtoons() {
    const xhr = new XMLHttpRequest();
    xhr.open('GET', '/api/webtoons?limit=20', true);
    xhr.onload = function() {
        if (xhr.status === 200) {
            const webtoons = JSON.parse(xhr.responseText);
            webtoons.forEach(addWebtoonItem);
        } else {
            console.error('추가 웹툰 데이터를 불러오는데 실패했습니다.');
        }
    };
    xhr.onerror = function() {
        console.error('추가 웹툰 데이터를 불러오는 중 오류가 발생했습니다.');
    };
    xhr.send();
}

/* 웹툰 항목을 추가하는 함수 */
function addWebtoonItem(webtoon) {
    const webtoonItem = document.createElement('div');
    webtoonItem.className = 'webtoon-item';
    webtoonItem.innerHTML = `
            <div class="webtoon-thumb">
                <img src="${webtoon.image}" alt="${webtoon.title}">
            </div>
            <div class="webtoon-title">${webtoon.title}</div>
        `;
    webtoonList.appendChild(webtoonItem);
}

/* Intersection Observer 설정 */
const observer = new IntersectionObserver(entries => {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            loadMoreWebtoons();
            observer.unobserve(entry.target);
        }
    });
});

observer.observe(document.getElementById('observer-target'));
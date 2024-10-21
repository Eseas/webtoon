let page = 1;
let isLoading = true;  // 로딩 중 여부 체크

/* 웹툰 리스트 DOM 요소 */
const webtoonList = document.getElementById('webtoon-list');
const loadingIndicator = document.getElementById('loading-indicator');

/* 초기 웹툰 데이터 로딩 */
document.addEventListener('DOMContentLoaded', function() {
    loadInitialWebtoons();
});

/* 초기 웹툰 데이터 로딩 함수 (AJAX 사용) - 40개 로드 */
function loadInitialWebtoons() {
    loadingIndicator.style.display = 'block'; // 로딩 애니메이션 표시
    const xhr = new XMLHttpRequest();
    xhr.open('GET', '/api/webtoons?limit=40&page=1', true);
    xhr.onload = function() {
        if (xhr.status === 200) {
            const webtoons = JSON.parse(xhr.responseText);
            webtoons.forEach(addWebtoonItem);
            page++;
        } else {
            console.error('웹툰 데이터를 불러오는데 실패했습니다.');
        }
        isLoading = false;
        loadingIndicator.style.display = 'none'; // 로딩 애니메이션 숨김
    };
    xhr.onerror = function() {
        console.error('웹툰 데이터를 불러오는 중 오류가 발생했습니다.');
        isLoading = false;
        loadingIndicator.style.display = 'none'; // 로딩 애니메이션 숨김
    };
    xhr.send();
}

/* 추가 웹툰 데이터 로딩 함수 (AJAX 사용) - 20개 추가 로드 */
function loadMoreWebtoons() {
    if (isLoading) return;
    isLoading = true;
    loadingIndicator.style.display = 'block'; // 로딩 애니메이션 표시

    setTimeout(() => {
        const xhr = new XMLHttpRequest();
        xhr.open('GET', '/api/webtoons?limit=20&page=' + page, true);
        xhr.onload = function() {
            if (xhr.status === 200) {
                const webtoons = JSON.parse(xhr.responseText);
                page++;
                webtoons.forEach(addWebtoonItem);
            } else {
                console.error('추가 웹툰 데이터를 불러오는데 실패했습니다.');
            }
            isLoading = false;
            loadingIndicator.style.display = 'none'; // 로딩 애니메이션 숨김
        };
        xhr.onerror = function() {
            console.error('추가 웹툰 데이터를 불러오는 중 오류가 발생했습니다.');
            isLoading = false;
            loadingIndicator.style.display = 'none'; // 로딩 애니메이션 숨김
        };
        xhr.send();
    }, 500); // 500ms 지연
}

/* 웹툰 항목을 추가하는 함수 */
function addWebtoonItem(webtoon) {
    const webtoonItem = document.createElement('div');
    webtoonItem.className = 'webtoon-item';

    // 클릭하면 웹툰 상세 페이지로 이동하도록 이벤트 추가
    webtoonItem.addEventListener('click', function() {
        window.location.href = `/webtoon/detail?id=${webtoon.id}&platform=kakao`;
    });

    webtoonItem.innerHTML = `
        <div class="webtoon-thumb">
            <img src="/static/kakao_main_image/${webtoon.id}/${webtoon.id}.jpg" alt="${webtoon.title}">
        </div>
        <div class="webtoon-title">${webtoon.title}</div>
    `;

    webtoonList.appendChild(webtoonItem);
}


/* Intersection Observer 설정 */
const observer = new IntersectionObserver(entries => {
    entries.forEach(entry => {
        if (entry.isIntersecting && !isLoading) {
            // 데이터 로딩 함수 호출
            loadMoreWebtoons();
            // 타겟 요소를 옵저버에서 제거 후, 데이터 로딩 후 다시 관찰
            observer.unobserve(entry.target);
            setTimeout(() => {
                observer.observe(document.getElementById('observer-target'));
            }, 500);
        }
    });
});

/* DOMContentLoaded 이후 옵저버 설정 */
document.addEventListener('DOMContentLoaded', function() {
    observer.observe(document.getElementById('observer-target'));
});

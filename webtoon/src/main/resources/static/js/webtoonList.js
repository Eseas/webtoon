let currentPage = 1;  // 현재 페이지 번호
let isLoading = false;  // 데이터 로딩 상태

// 스크롤 감지
$(window).scroll(function() {
    if ($(window).scrollTop() + $(window).height() >= $(document).height()) {
        loadMoreData();  // 스크롤이 맨 밑에 도달하면 데이터 요청
    }
});

// 데이터 로드 함수
function loadMoreData() {
    if (isLoading) return;  // 중복 요청 방지
    isLoading = true;  // 로딩 중 상태로 변경

    $.ajax({
        url: '/api/getNextPage',  // 서버에 데이터를 요청할 URL
        type: 'GET',
        data: { page: currentPage },  // 페이지 번호 전송
        success: function(data) {
            let container = $('#post-container');
            data.posts.forEach(post => {
                let postElement = `<div class="webtoon-content">
                             <h2>${post.title}</h2>
                             <p>${post.content}</p>
                           </div>`;
                container.append(postElement);  // 받은 데이터를 화면에 추가
            });

            currentPage++;  // 다음 페이지로 증가
            isLoading = false;  // 로딩 상태 해제
        },
        error: function(err) {
            console.error('데이터 로딩 중 오류 발생:', err);
            isLoading = false;  // 로딩 상태 해제
        }
    });
}

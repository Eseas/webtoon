document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    const usernameInput = document.getElementById('id');
    const passwordInput = document.getElementById('pwd');
    const usernameError = document.getElementById('usernameError');
    const passwordError = document.getElementById('passwordError');

    loginForm.addEventListener('submit', function(event) {
        // 에러 메시지 초기화
        usernameError.textContent = '';
        passwordError.textContent = '';

        let isValid = true;

        // 아이디 검증
        if (usernameInput.value.trim() === '') {
            usernameError.textContent = '아이디를 입력해주세요.';
            isValid = false;
        }

        // 비밀번호 검증
        if (passwordInput.value.trim() === '') {
            passwordError.textContent = '비밀번호를 입력해주세요.';
            isValid = false;
        }

        // 에러 발생 시 폼 전송 막기
        if (!isValid) {
            event.preventDefault();
        }
    });
});

document.getElementById('google-login-btn').addEventListener('click', async function() {
    try {
        // 서버에서 Google OAuth 설정을 받아옴
        const response = await fetch('/api/google-config');
        const config = await response.json();

        const googleClientId = config.clientId;
        const googleRedirectUrl = config.redirectUrl;

        // 구글 OAuth 인증 URL 생성
        const url = 'https://accounts.google.com/o/oauth2/v2/auth?client_id='
            + googleClientId
            + '&redirect_uri='
            + googleRedirectUrl
            + '&response_type=code'
            + '&scope=email profile';

        // 팝업 창 열기
        window.open(url, '_blank', 'width=500,height=600');
    } catch (error) {
        console.error('Error fetching Google config:', error);
    }
});

document.getElementById('naver-login-btn').addEventListener('click', async function() {
    try {
        const response = await fetch('/api/naver-config');
        const config = await response.json();

        const naverClientId = config.clientId;
        const naverRedirectUrl = config.redirectUrl;

        const url = 'https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=' +
            naverClientId +
            '&redirect_uri=' +
            naverRedirectUrl +
            '&state=1234';
        // 팝업 창 열기
        const popup = window.open(url, '_blank', 'width=500,height=600');
    } catch (error) {
        console.error('Error fetching Naver config:', error);
    }
});

// 팝업을 열고 소셜 로그인을 처리하는 함수
document.getElementById('kakao-login-btn').addEventListener('click', async function() {
    try {
        // 서버에서 Kakao OAuth 설정을 받아옴
        const response = await fetch('/api/kakao-config');
        const config = await response.json();

        const kakaoClientId = config.clientId;
        const kakaoRedirectUrl = config.redirectUrl;

        // 카카오 OAuth 인증 URL 생성
        const url = 'https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=' +
            kakaoClientId +
            '&redirect_uri=' +
            kakaoRedirectUrl +
            '&state=test';

        // 팝업 창 열기
        const popup = window.open(url, '_blank', 'width=500,height=600');

        // 팝업에서 부모 창과의 통신을 위해 메시지 리스너 설정
        window.addEventListener('message', function(event) {
            if (event.origin !== window.location.origin) {
                // 메시지를 보낸 창의 출처를 확인하여 보안 강화
                console.error('Invalid origin:', event.origin);
                return;
            }

            // 팝업에서 로그인 성공 후 전달된 메시지를 처리
            if (event.data && event.data.type === 'kakao-auth-success') {
                console.log('Login successful:', event.data.payload);
                // 이후 사용자가 인증되었음을 알리고 페이지를 갱신하거나 사용자의 정보를 화면에 반영
                window.location.reload(); // 예시로 페이지 리로드
            }
        });
    } catch (error) {
        console.error('Error fetching Kakao config:', error);
    }
});
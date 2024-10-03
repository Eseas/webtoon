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
        // 서버에서 Google OAuth 설정을 받아옴
        const response = await fetch('/api/naver-config');
        const config = await response.json();

        const naverClientId = config.clientId;
        const naverRedirectUrl = config.redirectUrl;

        // 구글 OAuth 인증 URL 생성
        const url = 'https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=' +
            naverClientId +
            '&redirect_uri=' +
            naverRedirectUrl +
            '&state=1234';
        console.log(url);
        // 팝업 창 열기
        window.open(url, '_blank', 'width=500,height=600');
    } catch (error) {
        console.error('Error fetching Naver config:', error);
    }
});

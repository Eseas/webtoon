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

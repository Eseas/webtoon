// 아이디 중복 확인 함수
function checkId() {
    const userId = document.getElementById("id").value;
    const idCheckMessage = document.getElementById("idCheckMessage");

    // 아이디가 입력되지 않은 경우 경고 메시지 표시
    if (userId === "") {
        idCheckMessage.innerText = "아이디를 입력하세요.";
        idCheckMessage.style.color = "red";
        return;
    }

    // 아이디 중복 확인을 위해 POST 요청
    fetch('/signup/checkid', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ id: userId })
    })
        .then(response => response.json())
        .then(data => {
            if (data.available) {
                // 사용 가능한 아이디인 경우
                idCheckMessage.innerText = "사용 가능한 아이디입니다.";
                idCheckMessage.style.color = "green";
            } else {
                // 이미 사용 중인 아이디인 경우
                idCheckMessage.innerText = "아이디가 이미 사용 중입니다.";
                idCheckMessage.style.color = "red";
            }
        })
        .catch(error => {
            console.error('Error:', error);
            idCheckMessage.innerText = "아이디 중복 확인 중 오류가 발생했습니다.";
            idCheckMessage.style.color = "red";
        });
}

// 회원가입 폼 검증 및 제출 함수
function checkForm(event) {
    event.preventDefault(); // 기본 폼 제출 동작을 방지합니다.

    const userId = document.getElementById("id").value;
    const password = document.getElementById("pwd").value;
    const name = document.getElementById("name").value;
    const birth = document.getElementById("birth").value;
    const idCheckMessage = document.getElementById("idCheckMessage").innerText;

    // 필수 필드가 비어 있는 경우 경고
    if (userId === "" || password === "" || name === "" || birth === "") {
        alert("모든 필드를 입력하세요.");
        return false;
    }

    // 아이디 중복 체크가 필요한 경우
    if (idCheckMessage === "아이디가 이미 사용 중입니다.") {
        alert("아이디 중복을 확인해주세요.");
        return false;
    }

    // 회원가입을 위한 PUT 요청
    fetch('/signup/process', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ id: userId, pwd: password, name: name, birth: birth })
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert("회원가입이 성공적으로 처리되었습니다.");
                window.location.href = "/signin"; // 가입 성공 시 로그인 페이지로 이동
            } else {
                alert("회원가입에 실패했습니다.");
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert("회원가입 중 오류가 발생했습니다.");
        });

    return true;
}

// 폼 제출 시 checkForm 함수 호출
document.getElementById("signupForm").addEventListener("submit", checkForm);
function checkId() {
    const userId = document.getElementById("id").value;
    const idCheckMessage = document.getElementById("idCheckMessage");

    if (userId === "") {
        idCheckMessage.innerText = "아이디를 입력하세요.";
        idCheckMessage.style.color = "red";
        return;
    }

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
                idCheckMessage.innerText = "사용 가능한 아이디입니다.";
                idCheckMessage.style.color = "green";
            } else {
                idCheckMessage.innerText = "아이디가 이미 사용 중입니다.";
                idCheckMessage.style.color = "red";
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
}


function checkForm() {
    const userId = document.getElementById("id").value;
    const password = document.getElementById("pwd").value;
    const name = document.getElementById("name").value;
    const birth = document.getElementById("birth").value;

    if (userId === "" || password === "" || name === "" || birth === "") {
        alert("모든 필드를 입력하세요.");
        return false;
    }

    const idCheckMessage = document.getElementById("idCheckMessage").innerText;

    if (idCheckMessage === "아이디가 이미 사용 중입니다.") {
        alert("아이디 중복을 확인해주세요.");
        return false;
    }

    fetch('/signup/insertDB', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ id: userId, pwd: password, name: name, birth:birth })
    })
        .then(response => response.json())
        .then(data => {
            // 회원가입 결과 로직
        })
        .catch(error => {
            console.error('Error:', error);
        });

    return true;
}

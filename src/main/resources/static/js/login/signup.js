/**
 * 아이디 중복 검증 함수
 */
async function checkLoginId() {
    const loginId = document.getElementById("loginId").value;
    if (!loginId) {
        setLoginIdMsg("");
        return;
    }
    const res = await fetch(`/check/loginId?loginId=${loginId}`);
    const isDuplicate = await res.json();
    setLoginIdMsg(isDuplicate ? "이미 존재하는 아이디입니다." : "사용 가능한 아이디입니다.", isDuplicate);
}

/**
 * 닉네임 중복 검증 함수
 */
async function checkNickname() {
    const nickname = document.getElementById("nickname").value;
    if (!nickname) {
        setNicknameMsg("");
        return;
    }
    const res = await fetch(`/check/nickname?nickname=${nickname}`);
    const isDuplicate = await res.json();
    setNicknameMsg(isDuplicate ? "이미 존재하는 닉네임입니다." : "사용 가능한 닉네임입니다.", isDuplicate);
}

/**
 * 로그인 검증 메시지
 */
function setLoginIdMsg(msg, isError) {
    const el = document.getElementById("loginId-msg");
    el.innerText = msg;
    el.style.color = isError ? "red" : "green";
}

/**
 * 닉네임 검증 메시지
 */
function setNicknameMsg(msg, isError) {
    const el = document.getElementById("nickname-msg");
    el.innerText = msg;
    el.style.color = isError ? "red" : "green";
}

/**
 * 비밀번호 검증 메시지
 */
function checkPasswordMatch() {
    const pw = document.getElementById("password").value;
    const pwConfirm = document.getElementById("password-confirm").value;
    const msgEl = document.getElementById("password-match-msg");

    if (!pwConfirm) {
        msgEl.innerText = "";
        return;
    }

    if (pw !== pwConfirm) {
        msgEl.innerText = "비밀번호가 일치하지 않습니다.";
        msgEl.style.color = "red";
    } else {
        msgEl.innerText = "";
    }
}

/**
 * 이메일 인증코드 전송 메시지
 */
async function sendCode() {
    const email = document.getElementById("email").value;
    const msgEl = document.getElementById("email-send-msg");
    const btn = event.target;

    if (!email) {
        msgEl.innerText = "이메일을 입력해주세요.";
        msgEl.style.color = "red";
        return;
    }

    // 클릭 시 안내 메시지 표시 + 버튼 비활성화
    btn.disabled = true;
    btn.textContent = "발송 중...";
    msgEl.innerText = "인증코드 발송 중입니다. 잠시만 기다려 주세요.";
    msgEl.style.color = "blue";

    try {
        // 서버 요청
        const res = await fetch("/email/send", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: "email=" + encodeURIComponent(email)
        });

        // 응답 메시지 표시
        const msg = await res.text();
        msgEl.innerText = msg;
        msgEl.style.color = res.ok ? "green" : "red";

    } catch (error) {
        msgEl.innerText = "서버와 연결할 수 없습니다. 잠시 후 다시 시도해 주세요.";
        msgEl.style.color = "red";
    } finally {
        // 버튼 복구
        btn.disabled = false;
        btn.textContent = "인증코드 발송";
    }
}

/**
 * 인증코드 검증
 */
async function verifyCode() {
    const email = document.getElementById("email").value;
    const verificationCode = document.getElementById("email-code").value;
    const msgEl = document.getElementById("email-verify-msg");

    if (!verificationCode) {
        msgEl.innerText = "인증 코드를 입력해주세요.";
        msgEl.style.color = "red";
        return;
    }

    const res = await fetch("/email/verify", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `email=${encodeURIComponent(email)}&verificationCode=${encodeURIComponent(verificationCode)}`
    });

    const msg = await res.text();

    if (res.ok) {
        msgEl.innerText = "이메일 인증에 성공하셨습니다.";
        msgEl.style.color = "green";
    } else {
        msgEl.innerText = msg;
        msgEl.style.color = "red";
    }
}

/**
 * 비밀번호 보이기 토글 이벤트
 */
function setPasswordVisual() {
    const passwordToggles = document.getElementsByClassName("password-toggle-button");
    Array.from(passwordToggles).forEach(passwordToggle => {
        const eyeIcon = passwordToggle.getElementsByClassName("eye-icon")[0];
        const eyeSlashIcon = passwordToggle.getElementsByClassName("eye-slash-icon")[0];
        passwordToggle.addEventListener("click", () => {
            const passwordInput = passwordToggle.parentElement.querySelector('input[type="password"], input[type="text"]');
            if (!passwordInput) return; // input이 없으면 중단

            if (passwordInput.type === "password") {
                passwordInput.type = "text";
                eyeIcon.classList.add("hidden");
                eyeSlashIcon.classList.remove("hidden");
            } else if (passwordInput.type === "text") {
                passwordInput.type = "password";
                eyeIcon.classList.remove("hidden");
                eyeSlashIcon.classList.add("hidden");
            }
        });
    })
}

/**
 * DOMContentLoaded 이벤트 리스너
 */
document.addEventListener("DOMContentLoaded", () => {
    setPasswordVisual();
});
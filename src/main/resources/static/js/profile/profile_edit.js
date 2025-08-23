document.addEventListener("DOMContentLoaded", () => {

        async function checkNickname() {
            const nickname = document.getElementById("nickname").value;
            const msgEl = document.getElementById("nickname-msg");

            if (!nickname) {
                msgEl.innerText = "닉네임을 입력하세요.";
                msgEl.style.color = "red";
                return;
            }

            try {
                const res = await fetch(`/api/profile/check/nickname?nickname=${encodeURIComponent(nickname)}&userIdx=${userIdx}`);
                const isDuplicate = await res.json();

                msgEl.innerText = isDuplicate ? "이미 존재하는 닉네임입니다." : "사용 가능한 닉네임입니다.";
                msgEl.style.color = isDuplicate ? "red" : "green";
            } catch (e) {
                msgEl.innerText = "서버와 연결할 수 없습니다.";
                msgEl.style.color = "red";
            }
        }

        async function sendCode(e) {
        const email = document.getElementById("email").value;
        const msgEl = document.getElementById("email-msg");
        const btn = e.target;

        if (!email) {
            msgEl.innerText = "이메일을 입력하세요.";
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

            // 성공하면 인증 코드 입력창 보이기
            if (res.ok) {
                document.getElementById("email-code-section").style.display = "block";
            }

        } catch (error) {
            msgEl.innerText = "서버와 연결할 수 없습니다. 잠시 후 다시 시도해 주세요.";
            msgEl.style.color = "red";
        } finally {
            // 버튼 복구
            btn.disabled = false;
            btn.textContent = "인증코드 발송";
        }
    }

    async function verifyCode(e) {
    	const email = document.getElementById("email").value;
    	const verificationCode = document.getElementById("emailCode").value;
    	const msgEl = document.getElementById("email-verify-msg");
    	const btn = e.target;

    	if (!verificationCode) {
    		msgEl.innerText = "인증 코드를 입력해주세요.";
    		msgEl.style.color = "red";
    		return;
    	}

    	try {
    		const res = await fetch("/email/verify", {
    			method: "POST",
    			headers: { "Content-Type": "application/x-www-form-urlencoded" },
    			body: `email=${encodeURIComponent(email)}&verificationCode=${encodeURIComponent(verificationCode)}`
    		});

    		if (res.ok) {
    			const data = await res.json();

    			if (data.verified) {
    				msgEl.innerText = "이메일 인증에 성공하셨습니다.";
    				msgEl.style.color = "green";
    				// 성공 시 입력 잠금
    				document.getElementById("email").readOnly = true;
    				document.getElementById("emailCode").readOnly = true;
    				// 성공 시 버튼 비활성화
    				document.getElementById("verify").disabled = true;
    				document.getElementById("emailSend").disabled = true;
    				btn.textContent = "인증 완료";
    			} else {
    				msgEl.innerText = msg;
    				msgEl.style.color = "red";
    			}
    	    }
    	} catch (e) {
    		msgEl.innerText = "서버와 연결할 수 없습니다. 잠시 후 다시 시도해 주세요.";
    		msgEl.style.color = "red";
    	}
    }

    window.checkNickname = checkNickname;
    window.sendCode = sendCode;
    window.verifyCode = verifyCode;
});
/**
 * 비밀번호 보이기 토글 이벤트
 */
function setPasswordVisual() {
    const passwordToggle = document.getElementsByClassName("password-toggle-button")[0];
    const eyeIcon = document.getElementsByClassName("eye-icon")[0];
    const eyeSlashIcon = document.getElementsByClassName("eye-slash-icon")[0];

    passwordToggle.addEventListener("click", () => {
        const passwordInput = document.getElementById("password");
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
}

/**
 * DOMContentLoaded 이벤트 리스너
 */
document.addEventListener("DOMContentLoaded", () => {
    setPasswordVisual();
});
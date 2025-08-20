/**
 * 삭제 동작 추가
 */
function addDeleteButtonEvent() {
    const deleteButton = document.getElementById("delete-button");
    if (deleteButton) {
        const id = deleteButton.dataset.id;

        deleteButton.addEventListener("click", async () => {
            const result = confirm("정말로 삭제하시겠습니까?");
            if (result) {
                fetch(`/api/portfolio/${id}/delete`, { method: "POST" })
                .then(async (res) => {
                    const body = await res.json();
                    if (res.ok) {
                        alert(body.message);
                        location.href = "/portfolio";
                    } else {
                        alert(body.message);
                    }
                }).catch(e => {
                    alert("요청 중 오류가 발생했습니다.");
                });
            }
        });
    }
}

/**
 * DOMContentLoaded 이벤트 리스너
 */
document.addEventListener("DOMContentLoaded", () => {
    addDeleteButtonEvent();
});
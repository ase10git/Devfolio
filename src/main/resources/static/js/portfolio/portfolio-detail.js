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
 * 좋아요 토글 동작
 */
function toggleLike() {
    let pending = false;
    let debounceTimer = null;
    const likeButton = document.getElementById("like-button");
    
        if (!likeButton) return;
    
        const likedImageEl = likeButton.querySelector(".like-image.liked");
        const unLikedImageEl = likeButton.querySelector(".like-image.un-liked");
        const portfolioId = likeButton.dataset.portfolioId;
        let currentLiked = likeButton.dataset.liked === "true";
        let clientSideLiked = currentLiked;
        let requestLiked = currentLiked;
    
        likeButton.addEventListener("click", function () {
            clientSideLiked = !clientSideLiked;
            updateUI();
        
            if (pending) {
                clearTimeout(debounceTimer);
            }
            pending = true;
            debounceTimer = setTimeout(() => {
                requestLiked = clientSideLiked;
                sendLikeRequest(portfolioId, requestLiked);
            }, 1000);
        });
    
        // 좋아요 상태에 따른 UI 업데이트
        function updateUI() {
            if (clientSideLiked) {
                likeButton.classList.add("active");
                likedImageEl.classList.remove("hidden");
                unLikedImageEl.classList.add("hidden");
            } else {
                likeButton.classList.remove("active");
                likedImageEl.classList.add("hidden");
                unLikedImageEl.classList.remove("hidden");
            }
        }
    
        // 좋아요 추가 혹은 제거 요청 전송
        async function sendLikeRequest(portfolioId, requestLiked) {
            pending = false;
            const url = requestLiked ? `/api/portfolio/${portfolioId}/add-like` : `/api/portfolio/${portfolioId}/remove-like`;
            if (currentLiked === requestLiked) return;
            try {
                await fetch(url, {method: "POST"})
                .then((res) => {
                    if (res.ok) return res.json();
                    else throw new Error(requestLiked ? "좋아요 등록 실패" : "좋아요 제거 실패");
                })
                .then((data) => {
                    currentLiked = data.liked;
                });
            } catch (e) {
                clientSideLiked = !clientSideLiked;
                updateUI();
                alert("좋아요 등록/제거 동작에 실패했습니다");
            }
        }
    
        window.addEventListener("beforeunload", () => {
        requestLiked = clientSideLiked;
        if (pending) {
            navigator.sendBeacon(requestLiked ? `/api/portfolio/${portfolioId}/add-like` : `/api/portfolio/${portfolioId}/remove-like`);
        }
        });
}

/**
 * 목록으로 가기 버튼 이벤트 리스너
 */
function addListButtonEvent() {
    const listButton = document.getElementById("back-button");
    if (listButton) {
        listButton.addEventListener("click", () => {
            location.href = "/portfolio";
        });
    }
}


/**
 * DOMContentLoaded 이벤트 리스너
 */
document.addEventListener("DOMContentLoaded", () => {
    addDeleteButtonEvent();
    toggleLike();
    addListButtonEvent();
});
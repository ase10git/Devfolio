document.addEventListener('DOMContentLoaded', function () {

    // '답글' 버튼 클릭 이벤트
    document.querySelectorAll('.btn-reply').forEach(button => {
        button.addEventListener('click', function() {
            const existingReplyForm = document.querySelector('.reply-form-container .comment-write-box');
            if (existingReplyForm) {
                existingReplyForm.remove();
            }

            const commentId = this.dataset.commentId;

            const commentItemContainer = this.closest('.comment-item');
            const replyFormContainer = commentItemContainer.querySelector('.replies .reply-form-container');

            if (replyFormContainer) {
                replyFormContainer.innerHTML = createReplyFormHtml(commentId);
                addEventListenersToReplyForm(replyFormContainer);
            } else {
                console.error('.reply-form-container를 찾을 수 없습니다.');
            }
        });
    });

    // '수정' 버튼 클릭 이벤트
    document.querySelectorAll('.btn-edit').forEach(button => {
        button.addEventListener('click', function() {
            const commentId = this.dataset.commentId;
            const commentItem = document.getElementById('comment-' + commentId);
            const contentDiv = commentItem.querySelector('.comment-content');
            showEditUI(contentDiv, commentId);
        });
    });

    // '삭제' 버튼 클릭 이벤트
    document.querySelectorAll('.btn-delete-comment').forEach(button => {
        button.addEventListener('click', function() {
            if (confirm('정말로 댓글을 삭제하시겠습니까?')) {
                const commentId = this.dataset.commentId;
                deleteComment(commentId);
            }
        });
    });

    /**
     * 대댓글 작성 폼의 HTML 문자열을 생성하는 함수
     * @param {string} parentId - 부모 댓글의 ID
     * @returns {string} - 생성된 HTML 문자열
     */
    function createReplyFormHtml(parentId) {
        const mainForm = document.getElementById('main-comment-form');
        // 비로그인 상태 등 메인 폼이 없는 경우를 대비
        if (!mainForm) return '';

        const formAction = mainForm.getAttribute('action');

        return `
            <div class="comment-write-box">
                <form action="${formAction}" method="post" class="comment-form">
                    <input type="hidden" name="parentId" value="${parentId}" />
                    <textarea name="content" placeholder="대댓글을 작성해주세요" required></textarea>
                    <div class="comment-form-actions">
                        <button type="button" class="btn-cancel-reply">취소</button>
                        <button type="submit" class="btn-submit">작성</button>
                    </div>
                </form>
            </div>
        `;
    }

    /**
     * 동적으로 생성된 대댓글 폼 내의 요소들에 이벤트를 추가하는 함수
     * @param {HTMLElement} container - 대댓글 폼을 감싸는 컨테이너
     */
    function addEventListenersToReplyForm(container) {
        const replyFormBox = container.querySelector('.comment-write-box');
        if (!replyFormBox) return;

        // '취소' 버튼 클릭 시 폼(을 감싸는 div) 제거
        replyFormBox.querySelector('.btn-cancel-reply').addEventListener('click', () => {
            replyFormBox.remove();
        });

        // 폼 제출 시 유효성 검사
        replyFormBox.querySelector('form').addEventListener('submit', (e) => {
            const textarea = replyFormBox.querySelector('textarea');
            if (textarea.value.trim() === '') {
                alert('내용을 입력해주세요.');
                e.preventDefault(); // 제출 막기
            }
        });
    }

    /**
     * 댓글 수정 UI를 생성하고 표시하는 함수
     * @param {HTMLElement} contentDiv - 수정할 내용이 표시된 div
     * @param {string} commentId - 수정할 댓글의 ID
     */
    function showEditUI(contentDiv, commentId) {
        const originalText = contentDiv.textContent.trim();
        contentDiv.innerHTML = `
            <div class="edit-area">
                <textarea class="edit-textarea">${originalText}</textarea>
                <div class="edit-actions">
                    <button type="button" class="btn-cancel-edit">취소</button>
                    <button type="button" class="btn-save-edit">저장</button>
                </div>
            </div>
        `;

        contentDiv.querySelector('.btn-save-edit').addEventListener('click', () => {
            const newContent = contentDiv.querySelector('.edit-textarea').value;
            updateComment(commentId, newContent, contentDiv, originalText);
        });

        contentDiv.querySelector('.btn-cancel-edit').addEventListener('click', () => {
            contentDiv.textContent = originalText;
        });
    }

    /**
     * 서버에 댓글 수정 요청을 보내는 함수 (fetch API 사용)
     */
    async function updateComment(commentId, content, contentDiv, originalText) {
        try {
            const response = await fetch('/api/community/comments', {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ commentId: commentId, content: content })
            });

            if (response.ok) {
                contentDiv.textContent = content;
            } else {
                const errorMessage = await response.text();
                alert('댓글 수정 실패: ' + errorMessage);
                contentDiv.textContent = originalText;
            }
        } catch (error) {
            console.error('Error:', error);
            alert('오류가 발생했습니다.');
        }
    }

    /**
     * 서버에 댓글 삭제 요청을 보내는 함수 (fetch API 사용)
     */
    async function deleteComment(commentId) {
        try {
            const response = await fetch(`/api/community/comments/${commentId}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                const commentElement = document.getElementById('comment-container-' + commentId);

                if (commentElement) {
                    const commentsToRemove = commentElement.querySelectorAll('.comment-item');
                    const deletedCount = 1 + commentsToRemove.length;

                    const countSpan = document.getElementById('comment-count');
                    if (countSpan) {
                        const currentCount = parseInt(countSpan.textContent, 10);
                        countSpan.textContent = Math.max(0, currentCount - deletedCount);
                    }

                    commentElement.remove();
                } else {
                    console.error('삭제할 댓글 요소를 찾지 못했습니다: comment-container-' + commentId);
                }
            } else {
                const errorMessage = await response.text();
                alert('댓글 삭제 실패: ' + errorMessage);
            }
        } catch (error) {
            console.error('Error:', error);
            alert('오류가 발생했습니다.');
        }
    }
});
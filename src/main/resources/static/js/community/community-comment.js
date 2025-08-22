// static/js/community/community_detail.js

document.addEventListener('DOMContentLoaded', function () {
    // 페이지에 댓글 작성 폼이 있는지 확인 (로그인 상태)
    const mainCommentFormContainer = document.querySelector('.comment-write-box');
    if (!mainCommentFormContainer) {
        return; // 폼이 없으면 (비로그인 상태) 스크립트 실행 중단
    }

    const mainCommentForm = mainCommentFormContainer.querySelector('.comment-form');
    const parentIdInput = mainCommentForm.querySelector('input[name="parentId"]');
    const textarea = mainCommentForm.querySelector('textarea');

    // '답글' 버튼 클릭 이벤트 처리
    document.querySelectorAll('.btn-reply').forEach(button => {
        button.addEventListener('click', function() {
            // 다른 곳에 열려있을 수 있는 대댓글 폼을 먼저 초기화
            resetCommentForm();

            const commentId = this.dataset.commentId;
            const commentItem = document.getElementById('comment-' + commentId);
            const replyFormContainer = commentItem.querySelector('.reply-form-container');

            // 1. parentId input의 값을 현재 댓글 ID로 설정
            parentIdInput.value = commentId;

            // 2. 메인 댓글 폼을 이 컨테이너로 이동시킴
            replyFormContainer.appendChild(mainCommentForm);

            // 3. 텍스트에리어에 포커스
            textarea.focus();

            // 4. 취소 버튼 추가
            addCancelButton();
        });
    });

    // '수정' 버튼 클릭 이벤트 처리 (임시)
    document.querySelectorAll('.btn-edit').forEach(button => {
        button.addEventListener('click', function() {
            alert('댓글 ID: ' + this.dataset.commentId + ' 수정 기능은 구현 예정입니다.');
        });
    });

    // '삭제' 버튼 클릭 이벤트 처리 (임시)
    document.querySelectorAll('.btn-delete').forEach(button => {
        button.addEventListener('click', function() {
            if (confirm('정말로 댓글을 삭제하시겠습니까?')) {
                alert('댓글 ID: ' + this.dataset.commentId + ' 삭제 기능은 구현 예정입니다.');
                // TODO: 여기에 fetch API 등을 사용하여 서버에 삭제 요청을 보내는 로직 추가
            }
        });
    });

    // 취소 버튼을 만들고 이벤트 리스너를 추가하는 함수
    function addCancelButton() {
        // 이미 취소 버튼이 있다면 새로 만들지 않음
        if (mainCommentForm.querySelector('.btn-cancel-reply')) {
            return;
        }

        const cancelButton = document.createElement('button');
        cancelButton.type = 'button';
        cancelButton.textContent = '취소';
        cancelButton.className = 'btn-cancel-reply';

        cancelButton.addEventListener('click', resetCommentForm); // [개선] 함수 직접 참조

        // '작성' 버튼 앞에 취소 버튼 삽입
        mainCommentForm.querySelector('button[type="submit"]').insertAdjacentElement('beforebegin', cancelButton);
    }

    // 댓글 폼을 원래 위치로 되돌리고 상태를 초기화하는 함수
    function resetCommentForm() {
        // 1. 폼을 원래의 컨테이너로 이동
        mainCommentFormContainer.appendChild(mainCommentForm);

        // 2. parentId 값을 비움
        parentIdInput.value = '';

        // 3. [개선] 텍스트에리어 내용도 비움
        textarea.value = '';

        // 4. 취소 버튼 제거
        const cancelButton = mainCommentForm.querySelector('.btn-cancel-reply');
        if (cancelButton) {
            cancelButton.remove();
        }
    }
});
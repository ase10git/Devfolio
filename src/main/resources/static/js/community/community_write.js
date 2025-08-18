document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('post-form');
    const isEditMode = form.dataset.isEditMode === 'true';

    let editor;

    // 제어할 요소들
    const categorySelect = document.getElementById('category');
    const statusGroup = document.getElementById('status-group'); // [핵심 1] "상태" 그룹 요소 가져오기
    const titleHelp = document.getElementById('title-help');
    const contentHelp = document.getElementById('content-help');

    // 스터디 가이드
    const studyGuideInitialValue = `
        <p><strong>스터디 목적 :</strong></p><p><br></p>
        <p><strong>예상 인원 :</strong></p><p><br></p>
        <p><strong>모집 기간 :</strong></p><p><br></p>
        <p><strong>운영 기간 :</strong></p><p><br></p>
        <p><strong>스터디 장소 :</strong></p><p><br></p>
        <p><strong>준비물 :</strong></p><p><br></p>
        <p><strong>가입 방법 :</strong></p>
    `;

    ClassicEditor
        .create(document.querySelector('#editor'), { language: 'ko' })
        .then(newEditor => {
            editor = newEditor;
            if (isEditMode) {
                editor.setData(document.querySelector('#editor').value);
            }
        })
        .catch(error => console.error(error));

    // 카테고리 변경 이벤트 리스너
    categorySelect.addEventListener('change', function () {
        // [핵심 2] "상태" 필드 표시/숨김 로직
        if (this.value === 'study') {
            statusGroup.style.display = 'flex'; // 보이기
        } else {
            statusGroup.style.display = 'none'; // 숨기기
        }

        // [핵심 3] 스터디 가이드 표시/숨김 로직 (새 글 작성 시에만)
        if (!isEditMode) {
            if (this.value === 'study' && editor.getData().trim() === '') {
                editor.setData(studyGuideInitialValue);
            } else if (this.value !== 'study' && editor.getData().trim() === studyGuideInitialValue.trim()) {
                editor.setData('');
            }
        }
    });

    // 폼 제출 이벤트 리스너 (기존과 동일)
    form.addEventListener('submit', function (e) {
        document.querySelector('#editor').value = editor.getData();
        let isValid = true;

        titleHelp.style.visibility = 'hidden';
        contentHelp.style.visibility = 'hidden';

        if (document.getElementById('title').value.trim() === '') {
            titleHelp.style.visibility = 'visible';
            isValid = false;
        }
        if (editor.getData().trim() === '' || editor.getData().trim() === '<p>&nbsp;</p>') {
            contentHelp.style.visibility = 'visible';
            isValid = false;
        }
        if (!isValid) {
            e.preventDefault();
        }
    });
});
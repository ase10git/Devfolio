// community-write.js

import { editorConfig } from 'ckeditor5Config';
import { ClassicEditor } from 'ckeditor5';

document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('post-form');
    const isEditMode = form.dataset.isEditMode === 'true';

    let editor;

    // [핵심 1] 두 개의 플래그를 사용하여 상태를 더 정밀하게 관리합니다.
    let isTemplateActive = false;     // 현재 템플릿이 적용된 상태인가?
    let isProgrammaticChange = false; // 코드로 내용을 변경 중인가? (잠금 역할)

    const categorySelect = document.getElementById('category');
    const statusGroup = document.getElementById('status-group');
    const titleHelp = document.getElementById('title-help');
    const contentHelp = document.getElementById('content-help');

    const studyGuideInitialValue = `
        <p><strong>스터디 목적 :</strong></p><p>&nbsp;</p>
        <p><strong>예상 인원 :</strong></p><p>&nbsp;</p>
        <p><strong>모집 기간 :</strong></p><p>&nbsp;</p>
        <p><strong>운영 기간 :</strong></p><p>&nbsp;</p>
        <p><strong>스터디 장소 :</strong></p><p>&nbsp;</p>
        <p><strong>준비물 :</strong></p><p>&nbsp;</p>
        <p><strong>가입 방법 :</strong></p>
    `;

    ClassicEditor
        .create(document.querySelector('#editor'), editorConfig)
        .then(newEditor => {
            editor = newEditor;
            if (isEditMode) {
                editor.setData(document.querySelector('#editor').value);
            }

            // [핵심 2] 에디터 내용 변경 감지 리스너
            editor.model.document.on('change:data', () => {
                // isProgrammaticChange가 true이면, 코드로 인한 변경이므로 무시하고 리턴
                if (isProgrammaticChange) {
                    return;
                }
                // 사용자에 의한 변경이 발생하면, 템플릿 상태를 비활성화
                isTemplateActive = false;
            });
        })
        .catch(error => console.error(error));

    // 카테고리 변경 이벤트 리스너
    categorySelect.addEventListener('change', function () {
        statusGroup.style.display = this.value === 'study' ? 'flex' : 'none';

        if (!isEditMode) {
            if (this.value === 'study' && editor.getData().trim() === '') {
                // 1. 템플릿 삽입 전 '잠금' 플래그를 true로 설정
                isProgrammaticChange = true;
                editor.setData(studyGuideInitialValue);
                // 2. 템플릿 상태를 '활성'으로 변경
                isTemplateActive = true;
                // 3. 작업이 끝났으므로 '잠금' 플래그를 false로 해제
                isProgrammaticChange = false;
            } else if (this.value !== 'study' && isTemplateActive) {
                // 4. 다른 카테고리 선택 시, 템플릿이 '활성' 상태이면 (수정 안됐으면) 에디터를 비움
                isProgrammaticChange = true;
                editor.setData('');
                isTemplateActive = false; // 템플릿 상태 비활성화
                isProgrammaticChange = false;
            } else {
                // 사용자가 이미 내용을 수정한 상태에서 카테고리를 바꾸는 경우
                isTemplateActive = false;
            }
        }
    });

    // 폼 제출 시 유효성 검사 (기존과 동일)
    form.addEventListener('submit', function (e) {
        document.querySelector('#editor').value = editor.getData();
        let isValid = true;

        titleHelp.style.visibility = 'hidden';
        contentHelp.style.visibility = 'hidden';

        if (document.getElementById('title').value.trim() === '') {
            titleHelp.style.visibility = 'visible';
            isValid = false;
        }

        const editorContent = editor.getData().trim();
        if (editorContent === '' || editorContent === '<p>&nbsp;</p>') {
            contentHelp.style.visibility = 'visible';
            isValid = false;
        }

        if (!isValid) {
            e.preventDefault();
        }
    });
});
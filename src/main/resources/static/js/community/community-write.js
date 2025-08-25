import { editorConfig } from 'ckeditor5Config';
import { ClassicEditor } from 'ckeditor5';

document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('post-form');
    const isEditMode = form.dataset.isEditMode === 'true';

    let editor;

    let isTemplateActive = false;
    let isProgrammaticChange = false;

    const categorySelect = document.getElementById('category');
    const statusGroup = document.getElementById('status-group');
    const titleHelp = document.getElementById('title-help');
    const contentHelp = document.getElementById('content-help');
    const categoryHelp = document.getElementById('category-help');

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

            editor.model.document.on('change:data', () => {
                if (isProgrammaticChange) {
                    return;
                }
                isTemplateActive = false;
            });
        })
        .catch(error => console.error(error));

    categorySelect.addEventListener('change', function () {
        statusGroup.style.display = this.value === 'study' ? 'flex' : 'none';

        if (!isEditMode) {
            if (this.value === 'study' && editor.getData().trim() === '') {
                isProgrammaticChange = true;
                editor.setData(studyGuideInitialValue);
                isTemplateActive = true;
                isProgrammaticChange = false;
            } else if (this.value !== 'study' && isTemplateActive) {
                isProgrammaticChange = true;
                editor.setData('');
                isTemplateActive = false;
                isProgrammaticChange = false;
            } else {
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
        categoryHelp.style.visibility = 'hidden';

        if (document.getElementById('title').value.trim() === '') {
            titleHelp.style.visibility = 'visible';
            isValid = false;
        }

        if (categorySelect.value === '') {
            categoryHelp.style.visibility = 'visible';
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
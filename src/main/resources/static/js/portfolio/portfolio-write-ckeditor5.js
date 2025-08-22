// CKEditor 설정
import { editorConfig } from 'ckeditor5Config';
import { ClassicEditor } from 'ckeditor5';

/**
 * CKEditor 초기화 함수
 */
function initializeEditor() {
    editorConfig['ckfinder'] = { uploadUrl: '/api/image/upload?target=portfolio' };
    // 에디터 적용
    ClassicEditor
        .create(document.getElementById("editor"), editorConfig)
        .then(editor => {
            // 이미지 업로드 후 input 추가
            addImageInfoInput(editor);

            // 에디터에 등록한 이미지 제거 시 input 제거
            manageImageInfoInput(editor);

            window.editor = editor;
        })
        .catch(error => {
            console.error('CKEditor 초기화 중 오류 발생:', error);
        });
}

/**
 * CKEditor에 이미지 업로드 후 input 추가
 */
function addImageInfoInput(editor) {
    // 이미지 업로드 후 input 추가
    const imageUploadEditing = editor.plugins.get('ImageUploadEditing');

    imageUploadEditing.on('uploadComplete', (evt, { data, imageElement }) => {
        editor.model.change(writer => {
            writer.setAttribute('data-uploaded', 'true', imageElement);
        });

        const viewImg = editor.editing.mapper.toViewElement(imageElement);
        const domImg = editor.editing.view.domConverter.mapViewToDom(viewImg);

        if (domImg) {
            buildImageInput(data.default);
        }
    });
}

/**
 * CKEditor에 등록한 이미지 제거 시 input 제거
 */
function manageImageInfoInput(editor) {
    const model = editor.model;
    model.document.registerPostFixer(writer => {
        const changes = model.document.differ.getChanges();
        let handled = false;

        // CKEditor 내의 이벤트 감지
        for (const change of changes) {
            if ((change.name === 'imageBlock' || change.name === 'imageInline')) {
                if (change.type === 'remove') {
                    handled = true;
    
                    const removedImageSrc = change.attributes.get('src');
                    const container = document.getElementById('image-list-box');
                    if (container) {
                        const input = container.querySelector(`input[value="${removedImageSrc}"]`);
                        if (input) input.remove();
                    }
                    handled = false;
                    break;
                } else if (change.type === 'insert') {
                    const addedImageSrc = change.attributes.get('src');
                    if (addedImageSrc) buildImageInput(addedImageSrc);
                }
            }
        }
        return handled;
    });
}

/**
 * 이미지 src를 가진 input 태그 생성
 */
function buildImageInput(data) {
    const container = document.getElementById('image-list-box');
    if (container) {
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = 'images';
        input.value = data;
        container.appendChild(input);
    }
}

export default initializeEditor;
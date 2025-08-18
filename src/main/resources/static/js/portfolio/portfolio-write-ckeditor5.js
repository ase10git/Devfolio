// CKEditor 설정
import { editorConfig } from 'ckeditor5Config';
import { ClassicEditor } from 'ckeditor5';

/**
 * CKEditor 초기화 함수
 */
function initializeEditor() {
    // 초기값 설정
    editorConfig['initialData'] = '<p>Hello</p>';
    // Placeholder 설정
    editorConfig['placeholder'] = '내용을 입력해주세요';
    editorConfig['ckfinder'] = { uploadUrl: '/image/upload' };
    // 에디터 적용
    ClassicEditor
        .create(document.getElementById("editor"), editorConfig)
        .catch(error => {
            console.error('CKEditor 초기화 중 오류 발생:', error);
        });
}

export default initializeEditor;
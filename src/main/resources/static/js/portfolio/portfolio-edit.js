/**
 * CKEditor에 데이터 추가
 */
function setEditorData() {
    const editor = window.editor;
    if (editor) {
        editor.setData(document.getElementById("editor").value);
    }
}

/**
 * DOMContentLoaded 이벤트 리스너
 */
document.addEventListener("DOMContentLoaded", () => {
    setEditorData();
});
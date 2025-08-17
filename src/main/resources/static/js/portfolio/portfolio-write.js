import initializeEditor from "./portfolio-write-ckeditor5.js";

/**
 * form 검증
 */
function validateForm() {
    const form = document.getElementById("write-form");
    form.addEventListener("submit", (event) => {
        validateCategory(event);
    });
}

/**
 * 카테고리 체크박스 검증
 */
function validateCategory(event) {
    const categories = document.querySelectorAll(".category-list input[type='checkbox']");
    const checkedCategories = Array.from(categories).filter(category => category.checked);
    if (checkedCategories.length === 0) {
        event.preventDefault();
        const formError = document.getElementsByClassName("form-error category")[0];
        formError.classList.add("visible");
        formError.textContent = '카테고리를 선택해주세요';
    }
}

/**
 * 이미지 미리보기 및 삭제 동작
 */
function previewImage() {
    const preview = document.getElementById("preview-image");
    const thumbnailInput = document.getElementById("thumbnail");
    const imgRemoveButton = document.querySelector(".img-remove-button");

    thumbnailInput.addEventListener("change", () => {
        const file = thumbnailInput.files[0];
        if (file) addPreviewImage(preview, file, imgRemoveButton);
    });
    removePreviewImage(imgRemoveButton, preview, thumbnailInput);
}

/**
 * 이미지 미리보기
 */
function addPreviewImage(preview, file, removeButton) {
    preview.classList.add("visible");
    removeButton.classList.add("visible");
    const reader = new FileReader();
    reader.onload = function (event) {
        preview.src = event.target.result;
    };
    reader.readAsDataURL(file);
}

/**
 * 이미지 미리보기 제거
 */
function removePreviewImage(removeButton, preview, input) {
    removeButton.addEventListener("click", () => {
        preview.src = "#";
        preview.classList.remove("visible");
        input.value = "";
        removeButton.classList.remove("visible");
    });
}

/**
 * DOMContentLoaded 이벤트 리스너
 */
document.addEventListener("DOMContentLoaded", () => {
    initializeEditor();
    validateForm();
    previewImage();
});
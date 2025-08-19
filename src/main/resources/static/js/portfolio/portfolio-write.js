import initializeEditor from "./portfolio-write-ckeditor5.js";
import templateData from "./data/write-template.json" with { type: "json" };

/**
 * form 검증
 */
function validateForm() {
    const form = document.getElementById("write-form");
    form.addEventListener("submit", (event) => {
        validDate(event);
        validateCategory(event);
        validImageCount(event);
    });
}

/**
 * 카테고리 체크박스 검증
 */
function validateCategory(event) {
    const categories = document.querySelectorAll(".category-list input[type='checkbox']");
    const checkedCategories = Array.from(categories).filter(category => category.checked);
    const formError = document.getElementsByClassName("form-error category")[0];
    if (checkedCategories.length === 0) {
        event.preventDefault();
        formError.classList.add("visible");
        formError.textContent = '카테고리를 선택해주세요';
    }
}

/**
 * CKEditor 이미지 개수 제한
 */
function validImageCount(event) {
    const imageList = document.querySelector("#image-list-box");
    const images = imageList.querySelectorAll("input");
    const formError = document.getElementsByClassName("form-error editor")[0];
    if (images.length > 50) {
        event.preventDefault();
        formError.classList.add("visible");
        formError.textContent = "이미지는 최대 50개까지만 첨부할 수 있습니다";
    }
}

/**
 * 날짜 검증
 */
function validDate(event) {
    const startDate = document.getElementById("start-date").value;
    const endDate = document.getElementById("end-date").value;
    const formError = document.getElementsByClassName("form-error date")[0];
    if (startDate > endDate) {
        event.preventDefault();
        formError.classList.add("visible");
        formError.textContent = "프로젝트 기간이 잘못되었습니다.";
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
 * 포트폴리오 템플릿 추가 함수
 */
function addTemplate() {
    const templateList = document.getElementsByClassName("template-list")[0];
    const customTemplateLi = templateList.children[0];
    templateData.forEach((data, index) => {
        const elLi = document.createElement("li");
        const inputGroup = document.createElement("div");
        inputGroup.classList.add("radio-input-group");
        
        const radio = document.createElement("input");
        radio.type = "radio";
        radio.id = `template-${data.idx}`;
        radio.name = "template";
        radio.value = data.idx;
        
        const label = document.createElement("label");
        label.htmlFor = `template-${data.idx}`;
        label.textContent = `템플릿 ${data.idx+1}`;

        inputGroup.appendChild(radio);
        inputGroup.appendChild(label);
        elLi.appendChild(inputGroup);

        const templateDesc = document.createElement("span");
        templateDesc.textContent = data.description;
        elLi.appendChild(templateDesc);

        templateList.insertBefore(elLi, customTemplateLi);
    });
    addTemplateEvent();

    function addTemplateEvent() {
        const radios = document.querySelectorAll('input[name="template"]');
        radios.forEach(radio => {
            radio.addEventListener("change", () => {
                if (radio.checked) {
                    const key = radio.value;
                    const data = templateData[key];
                    const headings = data.headings;
                    addHeadingsToEditor(headings);
                }
            });
        });
    }

    function addHeadingsToEditor(headings) {
        
        if (window.editor) {
            const editor = window.editor;
    
            editor.model.change(writer => {
                const insertPosition = editor.model.document.selection.getLastPosition();
                console.log(insertPosition)

                headings.forEach(heading => {
                    const tagElement = writer.createElement(heading.tagType);
                    writer.insertText(heading.value, tagElement);
                    writer.append(tagElement, insertPosition);
                });
            });
        } else {
            setTimeout(() => addHeadingsToEditor(headings), 100);
        }
    }
}

/**
 * DOMContentLoaded 이벤트 리스너
 */
document.addEventListener("DOMContentLoaded", () => {
    initializeEditor();
    validateForm();
    previewImage();
    addTemplate();
});
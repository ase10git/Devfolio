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
        validDescription(event);
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
 * 상세 설명 빈 값 검증
 */
function validDescription(event) {
    const editor = window.editor;
    const formError = document.getElementsByClassName("form-error editor")[0];

    if (editor) {
        const data = editor.getData().trim();
        const textOnly = data.replace(/<[^>]*>/g, '').trim();
        if (!textOnly) {
            event.preventDefault();
            formError.classList.add("visible");
            formError.textContent = "상세 설명을 입력해주세요";
        }
    }
}

/**
 * 이미지 미리보기 및 삭제 동작
 */
function previewImage() {
    const preview = document.getElementById("preview-image");
    const thumbnailInput = document.getElementById("thumbnail");
    const imgRemoveButton = document.querySelector(".img-remove-button");

    updatedImageLabel();
    thumbnailInput.addEventListener("change", () => {
        const file = thumbnailInput.files[0];
        if (file) addPreviewImage(preview, file, imgRemoveButton);
    });
    removePreviewImage(imgRemoveButton, preview, thumbnailInput);
}

/**
 * 이미지 미리보기
 */
function addPreviewImage(preview, file, removeButton,) {
    preview.classList.add("visible");
    removeButton.classList.add("visible");
    updatedImageLabel();

    // 수정 페이지에서 원본 이미지 숨기기
    const originalImage = document.getElementById("original-image");
    if (originalImage != null) originalImage.classList.add("hidden");

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
        updatedImageLabel();
        preview.src = "#";
        preview.classList.remove("visible");
        input.value = "";
        removeButton.classList.remove("visible");

        // 수정 페이지에서 원본 이미지 표시하기
        const originalImage = document.getElementById("original-image");
        if (originalImage != null) originalImage.classList.remove("hidden");
    });
}

/**
 * 이미지 라벨 상태 업데이트
 */
function updatedImageLabel() {
    const imageLabel = document.getElementsByClassName("image-label")[0];
    const originalImage = document.getElementById("original-image");

    if (originalImage != null) {
        imageLabel.classList.add("hidden");
        originalImage.classList.remove("hidden");
    } else {
        imageLabel.classList.remove("hidden");
    }
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


    // 라디오 버튼에 이벤트 등록
    function addTemplateEvent() {
        const radios = document.querySelectorAll('input[name="template"]');
        radios.forEach(radio => {
            radio.addEventListener("change", () => {
                if (radio.checked) {
                    const key = radio.value;
                    if (key <= 2) {
                        // 만들어둔 템플릿 추가
                        const data = templateData[key];
                        const headings = data.headings;
                        addHeadingsToEditor(headings);
                    } 
                }
            });
        });
    }

    // 미리 지정된 템플릿 데이터를 CKEditor에 추가
    function addHeadingsToEditor(headings) {
        if (headings === null) {
            alert('추가할 템플릿 정보가 없습니다');
            return;
        }

        if (window.editor) {
            const editor = window.editor;
            editor.model.change(writer => {
                // 템플릿으로 지정한 heading 추가
                headings.forEach((heading, index) => {
                    // 에디터 요소 중 마지막 요소 위치 가져오기
                    const root = editor.model.document.getRoot();
                    const children = Array.from(root.getChildren());
                    const lastChild = children[children.length - 1];
                    const position = writer.createPositionAfter(lastChild);

                    // heading tag 생성
                    const tagName = (heading.tagType !== null && heading.tagType !== "heading") ? heading.tagType.toLowerCase() : "heading2";
                    const tagElement = writer.createElement(tagName);
                    writer.setAttribute("id", `heading-${index}`, tagElement);

                    // 텍스트로 추가
                    writer.insertText(heading.value, tagElement);
                    writer.append(tagElement, position);
                });
            });
        } else {
            setTimeout(() => addHeadingsToEditor(headings), 500);
        }
    }

    // AI 요청 상태
    let isAIRequesting = false;

    // AI 템플릿 추천 동작
    async function sendAITemplateRequest() {
        const aiTemplateButton = document.getElementById("template-ai");
        const overlay = document.getElementById("loading-overlay");

        aiTemplateButton.addEventListener("click", async () => {
            // 중복 요청 방지
            if (isAIRequesting) return;

            // 선택한 카테고리 정리
            const categoryList = document.getElementsByClassName("category-list")[0];
            const categorySelected = categoryList.querySelectorAll("input[type='checkbox']:checked ~ label");
            
            if (categorySelected === null || categorySelected.length === 0) {
                alert("카테고리를 선택해주세요");
                return;
            }

            const categoryNames = Array.from(categorySelected).map(label => label.textContent);
            const inputType = categoryNames.join(", ");

            const params = new URLSearchParams({type: inputType}).toString();
            
            // 요청 전송
            isAIRequesting = true;
            aiTemplateButton.disabled = true;
            overlay.classList.add("active");

            try {
                await sendRequest(params);
            } catch(e) {
                console.error(e);
                alert("요청 중 오류가 발생했습니다.")
            } finally {
                isAIRequesting = false;
                aiTemplateButton.disabled = false;
                overlay.classList.remove("active");
            }
        });

        async function sendRequest(params) {
            const res = await fetch(`/api/ai/portfolio-template?${params}`, { method: "GET" });
            const body = await res.json();
    
            if (res.ok) {
                addHeadingsToEditor(body);
            } else {
                throw new Error("요청 실패");
            }
        }
    }
    sendAITemplateRequest();
}

/**
 * 템플릿 토글 이벤트 리스너
 */
function addTemplateToggleEvent() {
    const templateBox = document.getElementsByClassName("select-content")[0];
    const toggleButton = document.getElementsByClassName("select-toggle-button")[0];
    toggleButton.addEventListener("click", () => {
        templateBox.classList.toggle("visible");
    });
}

/**
 * DOMContentLoaded 이벤트 리스너
 */
document.addEventListener("DOMContentLoaded", () => {
    initializeEditor();
    validateForm();
    previewImage();
    addTemplate();
    addTemplateToggleEvent();
});
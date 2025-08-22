const url = new URL(window.location.href);
const params = new URLSearchParams(window.location.search);

/**
 * 카테고리 탭 스타일 지정
 */
function setCategoryTabStyle() {
    const categorySidebar = document.querySelector(".category-sidebar");
    const categoryTabs = categorySidebar.querySelectorAll("li a");
    if (params.get("categoryName") === null) {
        categoryTabs[0].classList.add("active");
    } else {
        categoryTabs.forEach((tab) => {
            const category = tab.dataset.category;
            if (category === params.get("categoryName")) {
                tab.classList.add("active");
            }
        });
    }
}

/**
 * 정렬 버튼 이벤트 리스너
 */
function addSortButtonAction() {
    const sortTabs = document.querySelector(".sort-tabs");
    const sortButtons = sortTabs.querySelectorAll("button");
    sortButtons.forEach((button) => {
        button.addEventListener("click", () => {
            const sort = button.dataset.sort;
            const order = "DESC";

            const params = new URLSearchParams(window.location.search);
            const url = new URL(window.location);

            params.set("sort", sort);
            params.set("direction", order);

            url.search = params.toString();
            window.location.href = url.toString();
        });
    });
}

/**
 * DOMContentLoaded 이벤트 리스너
 */
document.addEventListener("DOMContentLoaded", () => {
    setCategoryTabStyle();
    addSortButtonAction();
});
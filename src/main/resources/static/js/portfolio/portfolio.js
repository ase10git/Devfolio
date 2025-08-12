import addCarouselListeners from "./portfolio-carousel.js";
import customSelect from "./portfolio-custom-select.js";

/**
 * 검색 폼 내의 입력창과 버튼 이벤트 리스너
 */
function addSearchFormAction() {
  const searchInput = document.getElementById("search-input");
  const resetButton = document.getElementById("reset-btn");

  function updateResetButtonVisibility() {
    if (searchInput.value) {
      resetButton.classList.add("visible");
    } else {
      resetButton.classList.remove("visible");
    }
  }

  updateResetButtonVisibility();

  searchInput.addEventListener("input", updateResetButtonVisibility);

  resetButton.addEventListener("click", () => {
    searchInput.value = "";
    updateResetButtonVisibility();
  });
}


/**
 * 정렬 버튼 이벤트 리스너
 */
function addSortButtonAction() {
  const sortButtons = document.querySelectorAll(".sort-btn");
  sortButtons.forEach((button) => {
    button.addEventListener("click", () => {
      const sort = button.dataset.sort;
      const order = "DESC";

      const params = new URLSearchParams(window.location.search);
      const url = new URL(window.location);

      params.set("sort", sort);
      params.set("order", order);

      url.search = params.toString();
      window.location.href = url.toString();
    });
  })
}

/**
 * DOMContentLoaded 이벤트 리스너
 */
document.addEventListener("DOMContentLoaded", () => {
  customSelect();
  addCarouselListeners();
  addSortButtonAction();
  addSearchFormAction();
});
import addCarouselListeners from "./portfolio-carousel.js";
import customSelect from "./portfolio-custom-select.js";
import createPortfolioCard from "./portfolio-card.js";

/**
 * 검색 폼 내의 입력창과 버튼 이벤트 리스너
 */
function addSearchFormAction() {
  const searchInput = document.getElementById("search-input");
  const resetButton = document.getElementById("reset-button");

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
  const sortButtons = document.querySelectorAll(".sort-button");
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
 * 무한 스크롤을 위한 Intersection Oberser 이벤트 리스너
 */
function addInfiniteScroll() {
  const params = new URLSearchParams(window.location.search);
  let page = parseInt(params.get("page") || 1);
  let isLoading = false;

  const portfolioSection = document.getElementsByClassName("portfolios")[0];
  const portfolioList = document.getElementById("portfolio-list");
  const spinner = makeSpinner();

  /**
   * 포트폴리오 데이터 로딩 표시용 스피너
   */
  function makeSpinner() {
    const spinnerBox = document.createElement("div");
    spinnerBox.classList.add("spinner-box");
    const spinner = document.createElement("div");
    spinner.classList.add("loader");
    spinnerBox.appendChild(spinner);
    return spinner;
  }

  /**
   * 포트폴리오 카드 생성
   */
  function makePortfolioCard(portfolio) {
    const portfolioCard = createPortfolioCard(portfolio);
    portfolioList.appendChild(portfolioCard);
  }

  /**
   * 데이터 추가 요청 실패 메시지
   */
  function makeScrollErrorMessage() {
    const errorBox = document.createElement("div");
    errorBox.classList.add("scroll-error-box");
    const errorText = document.createElement("p");
    errorText.textContent = "데이터를 추가 요청하는데 실패했습니다";
    errorBox.appendChild(errorText);
    return errorBox;
  }

  /**
   * 포트폴리오 로딩 시작 처리
   */
  function loadingStart() {
    isLoading = true;
    portfolioSection.appendChild(spinner);
  }

  /**
   * 포트폴리오 로딩 완료 처리
   */
  function loadingFinish() {
    isLoading = false;
    const spinnerElement = portfolioSection.querySelector('.loader');
    if (spinnerElement) {
      portfolioSection.removeChild(spinnerElement);
    }
  }

  /**
   * 포트폴리오 데이터 요청
   */
  function fetchPortfolioData() {
    params.set("page", page);
    const requestUrl = `/api/portfolio/list?${params.toString()}`;

    fetch(requestUrl, {
      method: "GET",
      headers: {
        Accept: "application/json",
      },
    })
      .then((res) => res.json())
      .then((data) => {
        if (data.length === 0) {
          loadingFinish();
          return;
        }
        
        data.forEach((portfolio) => {
          makePortfolioCard(portfolio);
        });
        page++;
        loadingFinish();

        observeLastItem(observer, portfolioList.children);
      })
      .catch(() => {
        const errorBox = makeScrollErrorMessage();
        portfolioSection.appendChild(errorBox);
        loadingFinish();
      });
  }

  /**
   * 포트폴리오 리스트 내의 마지막 요소 체크
   */
  function observeLastItem(io, items) {
    const lastItem = items[items.length - 1];
    io.observe(lastItem);
  }

  // Observer 생성
  const observer = new IntersectionObserver((entries, io) => {
    entries.forEach((entry) => {
      if (!isLoading && entry.isIntersecting) {
        io.unobserve(entry.target);
        loadingStart();
        fetchPortfolioData();
      }
    });
  }, {threshold: 0.8});

  observeLastItem(observer, portfolioList.children);
}

/**
 * DOMContentLoaded 이벤트 리스너
 */
document.addEventListener("DOMContentLoaded", () => {
  customSelect();
  addCarouselListeners();
  addSortButtonAction();
  addSearchFormAction();
  addInfiniteScroll();
});
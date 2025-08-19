/**
 * 포트폴리오 메인 페이지의 캐러셀 슬라이드
 */
let currentIndex = 0;

function updateCarousel() {
  const track = document.getElementById("carousel-track");
  const indicators = document.querySelectorAll(".carousel-indicators button");

  track.style.transform = `translateX(-${currentIndex * 100}%)`;

  indicators.forEach((btn, idx) => {
    btn.classList.toggle("active", idx === currentIndex);
  });
}

function goToSlide(button) {
  currentIndex = parseInt(button.dataset.index);
  updateCarousel();
}

function prevSlide() {
  const total = document.querySelectorAll(".carousel-item").length;
  currentIndex = (currentIndex - 1 + total) % total;
  updateCarousel();
}

function nextSlide() {
  const total = document.querySelectorAll(".carousel-item").length;
  currentIndex = (currentIndex + 1) % total;
  updateCarousel();
}

function addCarouselListeners() {
  const indicatorButtons = document.querySelectorAll(".indicator-buttons");
  const prevButton = document.getElementsByClassName("carousel-button prev")[0];
  const nextButton = document.getElementsByClassName("carousel-button next")[0];

  if (indicatorButtons.length !== 0) {
    indicatorButtons.forEach((button) => {
      button.addEventListener("click", () => {
        goToSlide(button);
      });
    });
  }

  if (prevButton && nextButton) {
    prevButton.addEventListener("click", prevSlide);
    nextButton.addEventListener("click", nextSlide);
  }
}

export default addCarouselListeners;

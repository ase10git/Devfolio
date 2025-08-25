/**
* 포트폴리오 카드 생성
*/
export default function createPortfolioCard(portfolio) {
  const portfolioCard = document.createElement("div");
  portfolioCard.className = "portfolio-card";

  // 링크
  const link = document.createElement("a");
  link.className = "portfolio-link";
  link.href = `/portfolio/${portfolio.portfolioIdx}`;

  // 이미지 박스
  const cardImgBox = document.createElement("div");
  cardImgBox.className = "card-img-box";
  const portfolioImg = document.createElement("img");
  portfolioImg.src = portfolio.imageUrl;
  portfolioImg.alt = portfolio.title;
  cardImgBox.appendChild(portfolioImg);

  // 카드 정보 박스
  const cardInfoBox = document.createElement("div");
  cardInfoBox.className = "card-info-box";

  // 작성자 정보
  const writerMeta = document.createElement("div");
  writerMeta.className = "writer-meta";

  // 작성자 프로필
  const writerProfile = document.createElement("div");
  writerProfile.className = "writer-profile";
  const profileImg = document.createElement("img");
  profileImg.src = portfolio.writer.profileImg;
  profileImg.alt = portfolio.writer.nickname;
  writerProfile.appendChild(profileImg);

  // 작성자 이름과 수정 날짜
  const writerInfo = document.createElement("div");
  writerInfo.className = "writer-info";
  const nicknameSpan = document.createElement("span");
  nicknameSpan.textContent = portfolio.writer.nickname;
  const dateSpan = document.createElement("span");
  dateSpan.textContent = portfolio.updatedAt;
  writerInfo.append(nicknameSpan, dateSpan);

  writerMeta.append(writerProfile, writerInfo);

  // 포트폴리오 정보
  const portfolioMeta = document.createElement("div");
  portfolioMeta.className = "portfolio-meta";

  const portfolioInfo = document.createElement("div");
  portfolioInfo.className = "portfolio-info";
  const title = document.createElement("p");
  title.className = "portfolio-title";
  title.textContent = portfolio.title;
  const description = document.createElement("p");
  description.className = "portfolio-description";
  description.textContent = portfolio.description;
  portfolioInfo.append(title, description);

  // 통계 박스
  const statsBox = document.createElement("div");
  statsBox.className = "stats-box";

  const statsData = [
    { src: "/assets/icon/hand-thumbs-up.svg",  alt: "좋아요 아이콘", name: "likeCount" },
    { src: "/assets/icon/eye.svg", alt: "조회수 아이콘", name: "views" },
    { src: "/assets/icon/chat.svg", alt: "댓글 아이콘", name: "commentCount" },
  ];

  statsData.forEach((stat) => {
    const statsDiv = document.createElement("div");
    statsDiv.className = "stats";
    const icon = document.createElement("img");
    icon.src = stat.src;
    icon.alt = stat.alt;
    const value = document.createElement("span");
    value.textContent = portfolio[stat.name];
    statsDiv.append(icon, value);
    statsBox.appendChild(statsDiv);
  });

  portfolioMeta.append(portfolioInfo, statsBox);
  cardInfoBox.append(writerMeta, portfolioMeta);

  // 구조 합치기
  link.append(cardImgBox, cardInfoBox);
  portfolioCard.appendChild(link);

  return portfolioCard;
}

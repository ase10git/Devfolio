package io.github.sunday.devfolio.service.portfolio;

import io.github.sunday.devfolio.dto.portfolio.PortfolioCommentDto;
import io.github.sunday.devfolio.dto.user.WriterDto;
import io.github.sunday.devfolio.entity.table.portfolio.PortfolioComment;
import io.github.sunday.devfolio.entity.table.user.User;
import io.github.sunday.devfolio.repository.portfolio.PortfolioCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 포트폴리오의 댓글 Entity를 관리하는 Service
 */
@Service
@RequiredArgsConstructor
public class PortfolioCommentService {
    private final PortfolioCommentRepository portfolioCommentRepository;
    
    // Todo : 포트폴리오 댓글 조회
    public List<PortfolioCommentDto> getPortfolioComments(Long portfolioIdx) {
        List<PortfolioComment> comments = portfolioCommentRepository.findAllByPortfolio_PortfolioIdx(portfolioIdx);
        // Todo : 대댓글 구조 반영
        return comments.stream().map(comment -> commentToDto(comment))
                .toList();
    }
    
    // Todo : 포트폴리오 댓글 추가
    
    // Todo : 포트폴리오 댓글 수정
    
    // Todo : 포트폴리오 댓글 삭제

    // Todo : 포트폴리오 대댓글 조회

    // Todo : 포트폴리오 대댓글 추가

    // Todo : 포트폴리오 대댓글 수정

    // Todo : 포트폴리오 대댓글 삭제

    // Todo : Entity To Dto
    private PortfolioCommentDto commentToDto(PortfolioComment comment) {
        return PortfolioCommentDto.builder()
                .commentIdx(comment.getCommentIdx())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .writer(userToWriterDto(comment.getUser()))
                .portfolioIdx(comment.getPortfolio().getPortfolioIdx())
                .build();
    }

    private WriterDto userToWriterDto(User user) {
        return WriterDto.builder()
                .userIdx(user.getUserIdx())
                .nickname(user.getNickname())
                .profileImg(user.getProfileImg())
                .build();
    }
}

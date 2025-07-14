package com.example.server.mapper;

import java.util.List;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.server.dto.BoardDTO;
import com.example.server.dto.BoardResponseDTO;
import com.example.server.entity.Board;
import com.example.server.entity.Like;
import com.example.server.entity.Member;
import com.example.server.repository.MemberRepository;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD) // Spring Bean으로 등록되도록 설정
public abstract class BoardMapper { // ⭐ abstract 클래스로 변경하여 @Autowired 필드 주입 가능

    @Autowired // ⭐ MemberRepository를 주입받아 memberId -> Member 변환에 사용
    protected MemberRepository memberRepository;

    // 1. BoardDTO -> Board 엔티티 변환
    // 게시글 생성 시 사용
    @Mapping(target = "id", ignore = true) // ID는 DB에서 자동 생성되므로 DTO의 ID는 무시
    @Mapping(target = "comments", ignore = true) // BoardDTO에는 comments 정보가 없으므로 무시
    @Mapping(target = "likesList", ignore = true) // BoardDTO에는 likesList 정보가 없으므로 무시
    @Mapping(target = "member", source = "memberId", qualifiedByName = "mapMemberIdToMember") // memberId를 Member 객체로 변환
    public abstract Board toEntity(BoardDTO boardDTO);

    // 2. Board 엔티티 -> BoardDTO 변환
    // 게시글 수정 후 반환 등 간단한 정보 전달 시 사용
    @Mapping(source = "member.id", target = "memberId") // Board 엔티티의 member.id를 DTO의 memberId로 매핑
    @Mapping(target = "createdDate", source = "createdDate") // BaseEntity의 createdDate 매핑
    public abstract BoardDTO toDto(Board board);

    // 3. Board 엔티티 -> BoardResponseDTO 변환 (상세 조회, 목록 조회 시 사용)
    // BoardResponseDTO는 더 많은 정보를 포함
    @Mapping(source = "member.id", target = "memberId") // Member 엔티티의 ID를 DTO의 memberId로 매핑
    @Mapping(source = "member.nickname", target = "nickname") // Member 엔티티의 nickname을 DTO의 nickname으로 매핑
    @Mapping(source = "likesList", target = "likesCount", qualifiedByName = "mapLikesListToCount") // likesList를
                                                                                                   // likesCount로 변환
    @Mapping(target = "likedByUser", ignore = true) // likedByUser는 외부에서 주입 (toDtoWithLikedStatus에서 처리)
    @Mapping(target = "createdDate", source = "createdDate") // BaseEntity의 createdDate 매핑
    public abstract BoardResponseDTO toResponseDto(Board board);

    // 4. List<Board> -> List<BoardResponseDTO> 변환
    public abstract List<BoardResponseDTO> toResponseDtoList(List<Board> boards);

    // 5. Board 엔티티와 좋아요 상태를 조합하여 BoardResponseDTO 생성 (핵심 메서드)
    // BoardService에서 이 메서드를 호출하여 likedByUser 값을 설정
    public BoardResponseDTO toDtoWithLikedStatus(Board board, boolean likedByUser) {
        BoardResponseDTO dto = toResponseDto(board); // 기본 BoardResponseDTO 매핑
        dto.setLikedByUser(likedByUser); // likedByUser 필드 설정 (BoardResponseDTO에 Setter 필요)
        return dto;
    }

    // ⭐ Custom Mapping Method: memberId -> Member 엔티티
    // BoardDTO의 memberId를 받아 Member 엔티티를 조회하여 반환합니다.
    @Named("mapMemberIdToMember")
    protected Member mapMemberIdToMember(Long memberId) {
        if (memberId == null) {
            return null;
        }
        // memberRepository를 사용하여 Member 엔티티를 조회합니다.
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member with ID " + memberId + " not found"));
    }

    // ⭐ Custom Mapping Method: likesList -> likesCount
    // List<Like>의 size를 Long 타입의 likesCount로 변환합니다.
    @Named("mapLikesListToCount")
    protected Long mapLikesListToCount(List<Like> likesList) {
        return likesList != null ? (long) likesList.size() : 0L;
    }
}
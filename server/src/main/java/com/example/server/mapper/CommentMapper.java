package com.example.server.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.server.dto.CommentsDTO;
import com.example.server.entity.Board;
import com.example.server.entity.Comments;
import com.example.server.repository.BoardRepository;
import com.example.server.repository.CommentsRepository;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD)
public abstract class CommentMapper {

    @Autowired
    protected BoardRepository boardRepository;

    @Autowired
    protected CommentsRepository commentsRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "board", source = "boardId", qualifiedByName = "mapBoardIdToBoard")
    @Mapping(target = "parent", source = "parentId", qualifiedByName = "mapParentIdToComment")
    @Mapping(target = "children", ignore = true)
    public abstract Comments toEntity(CommentsDTO dto);

    @Mapping(source = "board.id", target = "boardId")
    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "member.id", target = "memberId")
    @Mapping(source = "children", target = "children", qualifiedByName = "mapChildren")
    @Mapping(source = "createdDate", target = "createdDate")
    public abstract CommentsDTO toDto(Comments comment);

    public List<CommentsDTO> toDtoList(List<Comments> comments) {
        return comments.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Named("mapBoardIdToBoard")
    protected Board mapBoardIdToBoard(Long boardId) {
        if (boardId == null)
            return null;
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board with ID " + boardId + " not found"));
    }

    @Named("mapParentIdToComment")
    protected Comments mapParentIdToComment(Long parentId) {
        if (parentId == null)
            return null;
        return commentsRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent comment with ID " + parentId + " not found"));
    }

    @Named("mapChildren")
    protected List<CommentsDTO> mapChildren(List<Comments> children) {
        if (children == null)
            return List.of();
        return children.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}

package com.example.server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import com.example.server.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Procedure(name = "Board.getBoardPage")
    List<Board> getBoardList(@Param("p_page") int page, @Param("p_size") int size);

    List<Board> findTop5ByOrderByCreatedDateDesc();

    @Procedure(name = "Board.getTop5ByLikes", outputParameterName = "p_result")
    List<Board> getTop5ByLikes();

    @Procedure(procedureName = "INCREMENT_VIEW_COUNT")
    void incrementViewCount(@Param("p_board_id") Long boardId);
}

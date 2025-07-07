package com.example.server.entity;

import java.util.List;

import com.example.server.Base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureParameter;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@NamedStoredProcedureQuery(name = "Board.getBoardPage", procedureName = "get_board_page", parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_page", type = Integer.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_size", type = Integer.class),
        @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, name = "p_result", type = void.class)
}, resultClasses = Board.class)
public class Board extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String title;
    @NotNull
    private String content;
    private Long views; // 조회수
    private Long likes; // 좋아요

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Comments> comments;

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeContent(String content) {
        this.content = content;
    }

}

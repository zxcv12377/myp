package com.example.server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;

import com.example.server.dto.ChannelDTO;
import com.example.server.entity.Channel;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
    boolean existsByName(String name);

    @Procedure(name = "Channel.getTop10ChannelsByPostCount", outputParameterName = "p_result")
    List<ChannelDTO> findTop10ByOrderByBoardCount();
}

package com.example.server.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.server.dto.ChannelDTO;
import com.example.server.entity.Channel;
import com.example.server.repository.ChannelRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;

    public Channel create(ChannelDTO dto) {
        if (channelRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("이미 존재하는 채널명입니다.");
        }

        Channel channel = Channel.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .iconUrl(dto.getIconUrl())
                .subscribe(true)
                .status(true)
                .build();

        return channelRepository.save(channel);
    }

    public List<ChannelDTO> list() {
        return channelRepository.findTop10ByOrderByBoardCount();
    }

    public void delete(Long id) {
        channelRepository.deleteById(id);
    }

}

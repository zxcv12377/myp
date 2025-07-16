package com.example.server.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelDTO {
    private Long id;
    @NotBlank(message = "채널명을 입력해 주세욧!")
    private String name;
    private String description;
    private String iconUrl;
    private Boolean subscribe;
    private Boolean status;
}

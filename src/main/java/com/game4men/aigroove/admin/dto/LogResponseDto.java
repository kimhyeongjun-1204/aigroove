package com.game4men.aigroove.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.game4men.aigroove.common.entity.Log;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LogResponseDto {
    private Integer log_id;
    private LocalDateTime log_time;
    private String username;

    @JsonProperty("is_admin")
    private boolean is_admin;
    
    private Log.LogLevel log_level;
    private String message;
}

package com.game4men.aigroove.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DashboardDto {
    private long totalUsers = 0;
    private Integer dailyUsers = 0;

    @JsonProperty("total_song_uploads")
    private long totalSongUploads = 0;

    @JsonProperty("daily_song_uploads")
    private Integer dailySongUploads = 0;

    @JsonProperty("daily_inquirys")
    private Integer dailyInquirys = 0;

    @JsonProperty("average_model_time")
    private Integer averageModelTime = 0;

    private List<LogResponseDto> logs;


}

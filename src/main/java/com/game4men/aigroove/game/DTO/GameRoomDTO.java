package com.game4men.aigroove.game.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GameRoomDTO {
    @NotBlank
    private String room_code;
    private Integer host_id;
    private Integer guest_id;
    private Boolean has_guest;
    private Boolean is_download_complete;
    private Boolean is_game_started;
    @NotBlank
    private String play_file_key;
}

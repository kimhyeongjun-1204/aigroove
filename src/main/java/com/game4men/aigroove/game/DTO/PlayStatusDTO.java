package com.game4men.aigroove.game.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PlayStatusDTO {
    @NotNull
    private float currentProgress;
    @NotNull
    private float lastCheckpoint;
    private int deaths;
    private Boolean hasCleared;
}

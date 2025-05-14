package com.game4men.aigroove.game.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BadgeDTO {
    @NotNull
    private int code;
    @NotNull
    private int currentValue;
    private int maxValue;
    @NotNull
    private Boolean hasAchieved;
}

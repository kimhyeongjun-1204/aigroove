package com.game4men.aigroove.game.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class MapFile {
    private List<Double> TurnTimes;

    public MapFile(List<Double> turnTimes) {
        this.TurnTimes = turnTimes;
    }
}

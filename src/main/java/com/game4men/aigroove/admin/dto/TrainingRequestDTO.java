package com.game4men.aigroove.admin.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingRequestDTO {
    private String adminId;
    private List<Integer> trainDatasetIds;
    private TrainingParams params;
    private String version;
    private String keyUpdates;
}
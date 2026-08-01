package com.game4men.aigroove.admin.dto;

import java.util.List;

import lombok.Data;

@Data
public class TrainingSaveFormat {
    private String adminId;
    private List<String> trainDatasetPaths;
    private TrainingParams params;
    private String version;
    private String keyUpdates;
}
package com.game4men.aigroove.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingParams {
    private float learningRate;
    private int batchSize;
    private int epochNumber;
    private int inputSize;
    private int hiddenSize;
    private int numLayers;
}
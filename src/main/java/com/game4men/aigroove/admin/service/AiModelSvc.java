package com.game4men.aigroove.admin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.game4men.aigroove.admin.dto.ModelResponseDto;
import com.game4men.aigroove.admin.dto.TrainingRequestDTO;
import com.game4men.aigroove.admin.dto.TrainingSaveFormat;
import com.game4men.aigroove.common.entity.DatasetInfo;
import com.game4men.aigroove.common.entity.ModelInfo;
import com.game4men.aigroove.common.repository.DatasetInfoRepository;
import com.game4men.aigroove.common.repository.ModelInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiModelSvc {
    private final ModelInfoRepository aiModelRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DatasetInfoRepository datasetInfoRepository;

    public List<ModelResponseDto> getAllModelVersions(String adminId) {
        // 실제로는 adminId로 권한 체크 등 가능
        List<ModelInfo> modelInfos = aiModelRepository.findAllByOrderByModelIdDesc();

        System.out.println(modelInfos.get(0).getVersion());
        return modelInfos.stream().map(model -> new ModelResponseDto(
                model.getModelId(),
                model.getVersion(),
                model.getReleasedDate(),
                model.getAccuracy(),
                model.getF1Score(),
                model.getKeyUpdates(),
                model.isSelected())).collect(Collectors.toList());
    }

    // 특정 모델 리턴
    public ModelInfo getModelInfoById(Integer modelId) {
        return aiModelRepository.findById(modelId).orElse(null);
    }

    public void saveTrainingRequest(TrainingRequestDTO dto) throws IOException {
        String dir = System.getProperty("user.dir") + "/data/training_requests";
        Files.createDirectories(Paths.get(dir));

        // ID → 실제 경로 리스트로 변환
        List<String> datasetPaths = dto.getTrainDatasetIds().stream()
                .map(id -> datasetInfoRepository.findById(id)
                        .map(DatasetInfo::getUri)
                        .orElseThrow(() -> new IllegalArgumentException("Dataset not found: " + id)))
                .collect(Collectors.toList());

        // DTO를 JSON으로 저장할 형태로 가공
        TrainingSaveFormat saveFormat = new TrainingSaveFormat();
        saveFormat.setAdminId(dto.getAdminId());
        saveFormat.setTrainDatasetPaths(datasetPaths);
        saveFormat.setParams(dto.getParams());

        String filename = dir + "/train_" + System.currentTimeMillis() + ".json";
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filename), saveFormat);
    }

    @Transactional
    // 실행 모델 선택
    public ModelInfo selectModel(Integer modelId, String adminId) {
        // 1. 모든 모델 selected=false
        List<ModelInfo> allModels = aiModelRepository.findAll();
        for (ModelInfo model : allModels) {
            model.setSelected(false);
        }

        aiModelRepository.saveAll(allModels);
        // 2. 선택한 모델만 selected=true
        ModelInfo selectedModel = aiModelRepository.findById(modelId)
                .orElseThrow(() -> new IllegalArgumentException("모델을 찾을 수 없습니다."));
        selectedModel.setSelected(true);
        return aiModelRepository.save(selectedModel);
    }

    // 모델 삭제
    public void deleteModel(Integer modelId) {
        // 존재하는지 확인 후 삭제
        ModelInfo model = aiModelRepository.findById(modelId)
            .orElseThrow(() -> new IllegalArgumentException("모델을 찾을 수 없습니다."));
        aiModelRepository.delete(model);
    }
}

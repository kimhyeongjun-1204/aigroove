package com.game4men.aigroove.admin.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.UUID;
import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.game4men.aigroove.admin.dto.DatasetInfoDTO;
import com.game4men.aigroove.admin.dto.TrainingParams;
import com.game4men.aigroove.admin.dto.TrainingRequestDTO;
import com.game4men.aigroove.admin.dto.TrainingSaveFormat;
import com.game4men.aigroove.common.entity.DatasetInfo;
import com.game4men.aigroove.common.entity.ModelInfo;
import com.game4men.aigroove.common.entity.Log;
import com.game4men.aigroove.common.entity.Admin;
import com.game4men.aigroove.common.repository.DatasetInfoRepository;
import com.game4men.aigroove.common.repository.ModelInfoRepository;
import com.game4men.aigroove.common.repository.AdminRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiTrainSvc {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DatasetInfoRepository datasetInfoRepository;
    private final ModelInfoRepository modelInfoRepository;
    private final AdminRepository adminRepository;
    private final LogSvc logSvc;

    public String saveTrainingRequest(TrainingRequestDTO dto) throws IOException {
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
        saveFormat.setVersion(dto.getVersion());
        saveFormat.setKeyUpdates(dto.getKeyUpdates());

        UUID train_id = UUID.randomUUID();
        String filename = "/home/t25102/v0.9.4/train-files/" + train_id + "_data.json";
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filename), saveFormat);
        return train_id.toString();
    }

    public void startTraining(String train_id, List<Integer> trainDatasetIds) throws IOException, InterruptedException {
        String filename = "/home/t25102/v0.9.4/train-files/" + train_id + "_data.json";

        System.err.println(filename);

        String pythonScriptPath = "src/main/resources/python/main.py";
        List<String> command = new ArrayList<>();
        command.add("python");
        command.add(pythonScriptPath);
        command.add("--train_ref_dir");
        command.add(filename);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // 에러 출력도 함께

        // 백그라운드 실행
        Process process = processBuilder.start();
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[PYTHON] " + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                int exitCode = process.waitFor(); // 여기서 대기
                System.out.println("Python 종료됨, exit code: " + exitCode);

                if (exitCode == 0) {
                    afterTrainingSuccess(train_id, trainDatasetIds); // 성공 시 처리
                } else {
                    afterTrainingFailure(train_id); // 실패 시 처리
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public String getTrainingProgressJson(String trainId) throws IOException {
        String PROGRESS_FILE_PATH = "/home/t25102/v0.9.4/train-files/" + trainId + "_status.json";
        Path path = Paths.get(PROGRESS_FILE_PATH);
        
        if (!Files.exists(path)) {
            Map<String, Object> statusMap = new HashMap<>();
            statusMap.put("train_status", "waiting");
            statusMap.put("epoch", 0);
            statusMap.put("total_epochs", 0);
            return objectMapper.writeValueAsString(statusMap);
        }
        
        return Files.readString(path);
    }

    private void afterTrainingSuccess(String trainId, List<Integer> trainDatasetIds) throws IOException {
        String PROGRESS_FILE_PATH = "/home/t25102/v0.9.4/train-files/" + trainId + "_status.json";
        String REQUEST_FILE_PATH = "/home/t25102/v0.9.4/train-files/" + trainId + "_data.json";
        String RESULT_FILE_PATH = "/home/t25102/v0.9.4/train-files/" + trainId + "_result.json";

        // JSON 읽기
        String jsonContent = Files.readString(Paths.get(PROGRESS_FILE_PATH));
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> statusMap = objectMapper.readValue(jsonContent, Map.class);
        statusMap.put("train_status", "success");
        String updatedJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(statusMap);
        Files.writeString(Paths.get(PROGRESS_FILE_PATH), updatedJson);

        jsonContent = Files.readString(Paths.get(REQUEST_FILE_PATH));
        statusMap = objectMapper.readValue(jsonContent, Map.class);
        String version = (String) statusMap.get("version");
        String keyUpdates = (String) statusMap.get("keyUpdates");
        String adminId = (String) statusMap.get("adminId");

        TrainingSaveFormat dto = objectMapper.readValue(new File(REQUEST_FILE_PATH), TrainingSaveFormat.class);
        TrainingParams params = dto.getParams();
        float learningRate = params.getLearningRate();
        int batchSize = params.getBatchSize();
        int epochNumber = params.getEpochNumber();
        int inputSize = params.getInputSize();
        int hiddenSize = params.getHiddenSize();
        int numLayers = params.getNumLayers();

        jsonContent = Files.readString(Paths.get(RESULT_FILE_PATH));
        statusMap = objectMapper.readValue(jsonContent, Map.class);
        float accuracy = ((Double) statusMap.get("accuracy")).floatValue();
        float f1Score = ((Double) statusMap.get("f1_score")).floatValue();
        int average_model_time = (int) statusMap.get("model_time");

        List<DatasetInfo> datasets = new ArrayList<>();
        for (int datasetId : trainDatasetIds) {
            datasets.add(findDatasetInfoById(datasetId));
        }
        ModelInfo modelInfo = new ModelInfo();
        modelInfo.setVersion(version);
        modelInfo.setReleasedDate(LocalDate.now());
        modelInfo.setKeyUpdates(keyUpdates);

        modelInfo.setAccuracy(accuracy);
        modelInfo.setF1Score(f1Score);
        modelInfo.setAverage_model_time(average_model_time);

        modelInfo.setLearningRate(learningRate);
        modelInfo.setBatchSize(batchSize);
        modelInfo.setEpochNumber(epochNumber);
        modelInfo.setInputSize(inputSize);
        modelInfo.setHiddenSize(hiddenSize);
        modelInfo.setNumLayers(numLayers);

        modelInfo.setDatasets(datasets);
        modelInfo.setSelected(false);
        modelInfoRepository.save(modelInfo);

        // 로그 기록
        Admin admin = adminRepository.findById(Integer.parseInt(adminId)).orElse(null);
        if (admin != null) {
            logSvc.saveLog(
                "AI 모델 학습이 완료되었습니다. (버전: " + version + ", 정확도: " + accuracy + "%, F1-Score: " + f1Score + ")",
                admin,
                Log.LogLevel.INFO
            );
        }
    }

    private void afterTrainingFailure(String trainId) throws IOException {
        String PROGRESS_FILE_PATH = "/home/t25102/v0.9.4/train-files/" + trainId + "_status.json";
        String REQUEST_FILE_PATH = "/home/t25102/v0.9.4/train-files/" + trainId + "_data.json";
        
        // 상태 파일이 없으면 생성
        if (!Files.exists(Paths.get(PROGRESS_FILE_PATH))) {
            Map<String, Object> statusMap = new HashMap<>();
            statusMap.put("train_status", "fail");
            statusMap.put("progress", 0);
            String updatedJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(statusMap);
            Files.writeString(Paths.get(PROGRESS_FILE_PATH), updatedJson);
        } else {
            // 기존 상태 파일 업데이트
            String jsonContent = Files.readString(Paths.get(PROGRESS_FILE_PATH));
            Map<String, Object> statusMap = objectMapper.readValue(jsonContent, Map.class);
            statusMap.put("train_status", "fail");
            String updatedJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(statusMap);
            Files.writeString(Paths.get(PROGRESS_FILE_PATH), updatedJson);
        }

        // 로그 기록
        String jsonContent = Files.readString(Paths.get(REQUEST_FILE_PATH));
        Map<String, Object> statusMap = objectMapper.readValue(jsonContent, Map.class);
        String version = (String) statusMap.get("version");
        String adminId = (String) statusMap.get("adminId");

        Admin admin = adminRepository.findById(Integer.parseInt(adminId)).orElse(null);
        if (admin != null) {
            logSvc.saveLog(
                "AI 모델 학습이 실패하였습니다. (버전: " + version + ")",
                admin,
                Log.LogLevel.ERROR
            );
        }
    }

    private DatasetInfo findDatasetInfoById(Integer datasetId) {
        return datasetInfoRepository.findById(datasetId).orElse(null);
    }
}

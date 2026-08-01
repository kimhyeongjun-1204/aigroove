package com.game4men.aigroove.admin.controller;

import com.game4men.aigroove.admin.dto.ModelResponseDto;
import com.game4men.aigroove.admin.dto.TrainingRequestDTO;
import com.game4men.aigroove.admin.service.AiModelSvc;
import com.game4men.aigroove.admin.service.LogSvc;
import com.game4men.aigroove.common.entity.ModelInfo;
import com.game4men.aigroove.common.entity.Log;
import com.game4men.aigroove.common.entity.Admin;
import com.game4men.aigroove.common.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/ai/model")
public class AiModelController {
    private final AiModelSvc aiModelSvc;
    private final LogSvc logSvc;
    private final AdminRepository adminRepository;

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllModelVersions(@RequestParam(name = "admin_id") String adminId) {
        try {
            List<ModelResponseDto> models = aiModelSvc.getAllModelVersions(adminId);

            Map<String, Object> response = new HashMap<>();

            response.put("result_code", 201);
            response.put("models", models);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("result_code", 500);
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/detail")
    public ResponseEntity<Map<String, Object>> getModelDetail(@RequestParam("model_id") Integer modelId) {
        try {
            // 1. ModelInfo 조회
            ModelInfo model = aiModelSvc.getModelInfoById(modelId);

            if (model == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("result_code", 404);
                error.put("error", "Model not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            // 2. dataset_ids, params 등은 서비스에서 조회/가공 (여기선 예시)
            List<String> datasetIds = model.getDatasets()
                    .stream()
                    .map(ds -> ds.getDatasetId().toString())
                    .collect(Collectors.toList());
            Map<String, Object> params = new HashMap<>();
            params.put("learning_rate", model.getLearningRate());
            params.put("batch_size", model.getBatchSize());
            params.put("epoch_number", model.getEpochNumber());
            params.put("input_size", model.getInputSize());
            params.put("hidden_size", model.getHiddenSize());
            params.put("num_layers", model.getNumLayers());

            // 3. 응답 JSON 구성
            Map<String, Object> response = new HashMap<>();
            response.put("result_code", 200);
            response.put("version_name", model.getVersion());
            response.put("released_date", model.getReleasedDate());
            response.put("accuracy", model.getAccuracy());
            response.put("f1_score", model.getF1Score());
            response.put("key_updates", model.getKeyUpdates());
            response.put("dataset_ids", datasetIds);
            response.put("params", params);
            response.put("selected", model.isSelected());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("result_code", 500);
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping
    public ResponseEntity<String> handleTrainingRequest(@RequestBody TrainingRequestDTO dto) {
        try {
            aiModelSvc.saveTrainingRequest(dto);
            return ResponseEntity.ok("학습 요청 JSON 저장 완료");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("저장 실패: " + e.getMessage());
        }
    }
    
    @PostMapping("/select")
    public ResponseEntity<Map<String, Object>> selectModel(@RequestParam("model_id") Integer modelId,
            @RequestParam("admin_id") String adminId) {
        Map<String, Object> response = new HashMap<>();
        try {
            ModelInfo model = aiModelSvc.selectModel(modelId, adminId);
            Optional<Admin> admin = adminRepository.findById(Integer.parseInt(adminId));
            
            // 로그 기록
            logSvc.saveLog(
                "AI 모델 버전 " + model.getVersion() + "을(를) 선택하였습니다.",
                admin.get(),
                Log.LogLevel.INFO
            );
            
            response.put("result_code", 201);
            response.put("model", model);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("result_code", 500);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteModel(@RequestParam("model_id") Integer modelId,
            @RequestParam("admin_id") String adminId) {
        Map<String, Object> response = new HashMap<>();
        try {
            ModelInfo model = aiModelSvc.getModelInfoById(modelId);
            Admin admin = adminRepository.findById(Integer.parseInt(adminId)).get();

            aiModelSvc.deleteModel(modelId);
            
            // 로그 기록
            logSvc.saveLog(
                "AI 모델 버전 " + model.getVersion() + "을(를) 삭제하였습니다.",
                admin,
                Log.LogLevel.INFO
            );
            
            response.put("result_code", 200);
            response.put("message", "모델이 성공적으로 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("result_code", 500);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
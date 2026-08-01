package com.game4men.aigroove.admin.controller;

import com.game4men.aigroove.admin.dto.ModelResponseDto;
import com.game4men.aigroove.admin.dto.TrainingRequestDTO;
import com.game4men.aigroove.admin.service.AiModelSvc;
import com.game4men.aigroove.admin.service.AiTrainSvc;
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
@RequestMapping("/admin/ai/train")
public class AiTrainController {
    private final AiTrainSvc aiTrainSvc;
    private final LogSvc logSvc;
    private final AiModelSvc aiModelSvc;
    private final AdminRepository adminRepository;

    @PostMapping
    public ResponseEntity<String> handleTrainingRequest(@RequestBody TrainingRequestDTO dto) {
        try {
            String train_id = aiTrainSvc.saveTrainingRequest(dto);
            aiTrainSvc.startTraining(train_id, dto.getTrainDatasetIds());
            
            // 로그 기록
            Admin admin = adminRepository.findById(Integer.parseInt(dto.getAdminId())).get();
            logSvc.saveLog(
                "AI 모델 학습을 시작하였습니다. (버전: " + dto.getVersion() + ")",
                admin,
                Log.LogLevel.INFO
            );
            
            return ResponseEntity.ok(train_id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("저장 실패: " + e.getMessage());
        }
    }
    
    @GetMapping("/progress")
    public ResponseEntity<String> getTrainingProgress(
            @RequestParam("train_id") String trainId
    ) {
        try {
            String json = aiTrainSvc.getTrainingProgressJson(trainId);
            return ResponseEntity.ok().body(json);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"Unable to read progress.\"}");
        }
    }
}

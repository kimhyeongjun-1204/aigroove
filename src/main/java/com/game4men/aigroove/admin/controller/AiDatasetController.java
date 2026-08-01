package com.game4men.aigroove.admin.controller;

import com.game4men.aigroove.admin.dto.DatasetInfoDTO;
import com.game4men.aigroove.admin.service.AiDatasetSvc;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AiDatasetController {
    private final AiDatasetSvc aiDatasetSvc;

    @GetMapping("/dataset/all")
    public Map<String, Object> findAllAiDataSets() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<DatasetInfoDTO> datasets = aiDatasetSvc.findAllAiDataSets();
            response.put("data", datasets);
            response.put("result_code", 201);
        } catch (Exception e) {
            response.put("result_code", 400);
            response.put("message", "Failed to fetch datasets: " + e.getMessage());
            response.put("data", null);
        }

        return response;
    }

    @GetMapping("/dataset/latest-version")
    public Map<String, Object> getLatestVersion() {
        Map<String, Object> response = new HashMap<>();
        try {
            String latestVersion = aiDatasetSvc.getLatestVersion();
            response.put("data", latestVersion);
            response.put("result_code", 201);
        } catch (Exception e) {
            response.put("result_code", 400);
            response.put("message", "Failed to fetch latest version: " + e.getMessage());
            response.put("data", null);
        }
        return response;
    }   

    // 삭제 요청
    @DeleteMapping("/dataset")
    public Map<String, Object> deleteAiDataSet(@RequestParam("admin_id") Integer adminId,
            @RequestParam("dataset_id") Integer datasetId) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean deleted = aiDatasetSvc.deleteAiDataSet(adminId, datasetId);
            if (deleted) {
                response.put("result_code", 201);
            } else {
                response.put("result_code", 404);
                response.put("message", "데이터셋을 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            response.put("result_code", 400);
            response.put("message", "데이터셋 삭제 실패: " + e.getMessage());
        }
        return response;
    }

    @PostMapping(value = "/ai/dataset", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> uploadDatasetChunk(@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();
        try {
            Integer adminId = Integer.parseInt(payload.get("admin_id").toString());
            String name = payload.get("name").toString();
            String fileBase64 = payload.get("file").toString();
            int index = Integer.parseInt(payload.get("index").toString());
            int totalChunks = Integer.parseInt(payload.get("total_chunks").toString());

            // 실제 저장 로직은 서비스에 위임
            int uploadedChunks = aiDatasetSvc.saveDatasetChunk(adminId, name, fileBase64, index, totalChunks);
            response.put("result_code", 201);
            response.put("uploaded_chunks", uploadedChunks);
        } catch (Exception e) {
            response.put("result_code", 400);
            response.put("message", "청크 업로드 실패: " + e.getMessage());
        }
        return response;
    }

    @GetMapping("/ai/dataset/download")
    public ResponseEntity<Resource> downloadDataset(@RequestParam("dataset_id") Integer datasetId,
            @RequestParam("admin_id") Integer adminId) {
        try {
            // 1. 데이터셋 정보 조회
            DatasetInfoDTO datasetInfo = aiDatasetSvc.findDatasetInfoById(datasetId);
            if (datasetInfo == null) {
                return ResponseEntity.notFound().build();
            }

            // 2. 파일 경로 확인
            String filePath = datasetInfo.getUri();
            Path path = Path.of(filePath);
            if (!Files.exists(path)) {
                return ResponseEntity.notFound().build();
            }

            // 3. 다운로드 로그 기록
            aiDatasetSvc.logDatasetDownload(adminId, datasetInfo.getName());

            // 4. Resource 생성
            Resource resource = new FileSystemResource(path.toFile());

            // 5. 파일명 지정 (확장자 포함 여부 판단)
            String fileName = path.getFileName().toString(); // 실제 zip 파일명 사용

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/zip"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + ".zip\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

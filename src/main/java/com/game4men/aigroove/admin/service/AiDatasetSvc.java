package com.game4men.aigroove.admin.service;

import com.game4men.aigroove.admin.dto.DatasetInfoDTO;
import com.game4men.aigroove.common.entity.DatasetInfo;
import com.game4men.aigroove.common.entity.Admin;
import com.game4men.aigroove.common.entity.ModelInfo;
import com.game4men.aigroove.common.entity.Log;
import com.game4men.aigroove.common.repository.DatasetInfoRepository;
import com.game4men.aigroove.common.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import java.time.LocalDate;
import com.game4men.aigroove.common.repository.ModelInfoRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AiDatasetSvc {
    private final DatasetInfoRepository datasetInfoRepository;
    private final AdminRepository adminRepository;
    private final ModelInfoRepository modelInfoRepository;
    private final LogSvc logSvc;

    /* 1. 데이터셋 목록 조회 — Fetch Join으로 N+1 문제 해결 (기존: 1+N회 쿼리 → 개선: 1회) */
    public List<DatasetInfoDTO> findAllAiDataSets() {
        List<DatasetInfo> datasets = datasetInfoRepository.findAllWithUploader();
        return datasets.stream()
                .map(DatasetInfoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // 데이터셋 다운로드 로그 기록
    public void logDatasetDownload(Integer adminId, String datasetName) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관리자입니다."));
        
        logSvc.saveLog(
            "AI 데이터셋을 다운로드하였습니다. (이름: " + datasetName + ")",
            admin,
            Log.LogLevel.INFO
        );
    }

    // 특정 데이터셋 조회
    public DatasetInfoDTO findDatasetInfoById(Integer datasetId) {
        return datasetInfoRepository.findById(datasetId)
                .map(DatasetInfoDTO::fromEntity)
                .orElse(null);
    }

    /* 2. 최신 버전 조회 */
    public String getLatestVersion() {
        ModelInfo latestModel = modelInfoRepository.findTopByOrderByModelIdDesc();
        return latestModel.getVersion();
    }

    /* 3. 특정 데이터셋 삭제 */
    @Transactional
    public boolean deleteAiDataSet(Integer adminId, Integer datasetId) {
        try {
            DatasetInfo datasetInfo = datasetInfoRepository.findById(datasetId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 데이터셋입니다."));
            Admin admin = adminRepository.findById(adminId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 관리자입니다."));
            
            System.out.println("관리자 이름 : " + datasetInfo.getUploader().getName() + "");
            datasetInfo.setUploader(null);
            datasetInfoRepository.save(datasetInfo);
            datasetInfoRepository.delete(datasetInfo);
            
            // 로그 기록
            logSvc.saveLog(
                "AI 데이터셋을 삭제하였습니다. (이름: " + datasetInfo.getName() + ")",
                admin,
                Log.LogLevel.INFO
            );
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int saveDatasetChunk(Integer adminId, String name, String fileBase64, int index, int totalChunks) throws IOException {
        // 1. 임시 디렉토리에 청크 저장
        String tempDir = System.getProperty("java.io.tmpdir");
        String chunkFileName = tempDir + File.separator + adminId + "_" + name + ".part" + index;
        byte[] chunkBytes = Base64.getDecoder().decode(fileBase64);

        try (FileOutputStream fos = new FileOutputStream(chunkFileName)) {
            fos.write(chunkBytes);
        }

        // 2. 마지막 청크 도착 시 zip 병합
        if (index == totalChunks) {
            // zip 파일명
            String zipFileName = name + ".zip";
            String zipTempPath = tempDir + File.separator + zipFileName;

            try (FileOutputStream fos = new FileOutputStream(zipTempPath);
                ZipOutputStream zos = new ZipOutputStream(fos)) {

                for (int i = 1; i <= totalChunks; i++) {
                    String partPath = tempDir + File.separator + adminId + "_" + name + ".part" + i;
                    Path partFilePath = Path.of(partPath);

                    // zip 내부 파일명 예: part1, part2 ...
                    ZipEntry entry = new ZipEntry("part" + i);
                    zos.putNextEntry(entry);
                    Files.copy(partFilePath, zos);
                    zos.closeEntry();

                    Files.delete(partFilePath); // 파트 파일 제거
                }
            }

            // 3. zip 파일을 영구 저장 디렉토리로 이동
            String destDir = "/home/t25102/v0.9/data/datasets/";
            File destDirFile = new File(destDir);
            if (!destDirFile.exists()) destDirFile.mkdirs();

            String destPath = destDir + File.separator + zipFileName;
            Files.move(Path.of(zipTempPath), Path.of(destPath), StandardCopyOption.REPLACE_EXISTING);

            // 4. DB에 등록
            DatasetInfo datasetInfo = new DatasetInfo();
            datasetInfo.setName(name);
            datasetInfo.setUploadDate(LocalDate.now());

            float sizeGb = Math.round((float) new File(destPath).length() / (1024 * 1024 * 1024) * 100.0f) / 100.0f;
            datasetInfo.setSize(sizeGb);
            datasetInfo.setUri(destPath); // zip 경로로 저장

            Admin uploader = adminRepository.findById(adminId).orElse(null);
            datasetInfo.setUploader(uploader);
            datasetInfoRepository.save(datasetInfo);

            // 로그 기록
            logSvc.saveLog(
                "AI 데이터셋을 업로드하였습니다. (이름: " + name + ", 크기: " + sizeGb + "GB)",
                uploader,
                Log.LogLevel.INFO
            );

            //5. 압축 해제
            unzip(Path.of(destPath), Path.of("/home/t25102/v0.9/data/datasets/"+name));
        }

        return index;
    }

    private void unzip(Path zipFilePath, Path targetDir) throws IOException {
        // 대상 폴더가 없으면 생성
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFilePath))) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                Path entryPath = targetDir.resolve(entry.getName()).normalize();
                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    // 상위 폴더 먼저 생성
                    Files.createDirectories(entryPath.getParent());

                    // 파일 복사
                    try (OutputStream os = Files.newOutputStream(entryPath)) {
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            os.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }
}

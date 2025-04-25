package com.game4men.aigroove.game.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class ThumbnailSvc {
    private static final Logger logger = LoggerFactory.getLogger(ThumbnailSvc.class);
    //@Value("${python.script.path}")
    private String pythonScriptPath = "src/main/resources/python/generate_image.py";
    //@Value("${output.image.directory}")
    private String outputImageDirectory = "./generated-images";
    
    @Async
    public CompletableFuture<String> generateImage(String prompt) {
        String imageId = UUID.randomUUID().toString();
        String outputPath = outputImageDirectory + "/" + imageId + ".png";
        
        // 출력 디렉토리가 존재하는지 확인하고 없으면 생성
        try {
            Path directory = Paths.get(outputImageDirectory);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
        } catch (IOException e) {
            logger.error("Failed to create output directory", e);
            return CompletableFuture.failedFuture(e);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                // Python 스크립트 실행 명령어 설정
                List<String> command = new ArrayList<>();
                command.add("python");  // 시스템에 따라 'python'이 될 수도 있음
                command.add(pythonScriptPath);
                command.add("--prompt");
                command.add(prompt);
                command.add("--output");
                command.add(outputPath);
                
                System.err.println(command);
                ProcessBuilder processBuilder = new ProcessBuilder(command);
                Process process = processBuilder.start();
                
                // 프로세스 출력 읽기
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    StringBuilder output = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                        logger.debug("Python output: {}", line);
                    }
                }
                
                // 에러 출력 읽기
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        logger.error("Python error: {}", line);
                    }
                }
                
                // 프로세스 완료 대기
                int exitCode = process.waitFor();

                if (exitCode != 0) {
                    throw new RuntimeException("Python script execution failed with exit code: " + exitCode);
                }
                
                // 생성된 이미지 파일이 존재하는지 확인
                Path imagePath = Paths.get(outputPath);
                if (!Files.exists(imagePath)) {
                    throw new RuntimeException("Generated image file not found");
                }
                
                return imageId;
            } catch (IOException | InterruptedException e) {
                logger.error("Error executing Python script", e);
                throw new RuntimeException("Failed to generate image", e);
            }
        });
    }

    public Resource getImageAsResource(String imageId) throws IOException {
        Path imagePath = Paths.get(outputImageDirectory, imageId + ".png");
        Resource resource = new UrlResource(imagePath.toUri());
        
        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new RuntimeException("Could not read image file: " + imageId);
        }
    }
}
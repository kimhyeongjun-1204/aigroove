package com.game4men.aigroove.game.controller;

import com.game4men.aigroove.game.service.AudioConvertSvc;
import com.game4men.aigroove.game.service.LogService;
import com.game4men.aigroove.game.service.ThumbnailSvc;
import com.game4men.aigroove.common.entity.User;
import com.game4men.aigroove.game.DTO.ImageGenerationResponse;
import com.game4men.aigroove.game.DTO.MapFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/game/song")
@Tag(name = "곡 등록 API", description = "곡 등록 관련 API")
public class SongController {
    private final LogService logSvc;
    private final ThumbnailSvc thumbnailSvc;
    private final AudioConvertSvc audioConverterService;

    @Operation(summary = "썸네일 생성 요청", description = "프롬프트를 통한 썸네일 생성 요청을 서버로 전송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성 성공", content = @Content(schema = @Schema(implementation = ImageGenerationResponse.class))),
            @ApiResponse(responseCode = "400", description = "오류"),
            @ApiResponse(responseCode = "401", description = "사용자가 인증되지 않았습니다.")
    })
    @PostMapping("/thumbnail/generate")
    public CompletableFuture<ResponseEntity<?>> generateImage(
            @RequestParam(name = "prompt", required = true) String prompt,
            HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        try {
            logSvc.createLog(0, user, "유저가 썸네일 생성을 요청했습니다.");
            return thumbnailSvc.generateImage(prompt)
                    .thenApply(imageId -> {
                        String imageUrl = "./generated_images/" + imageId;
                        return ResponseEntity
                                .ok(new ImageGenerationResponse(imageId, imageUrl, "Image generation completed"));
                    });

        } catch (Exception e) {
            logSvc.createLog(2, user, "썸네일 생성 요청이 실패했습니다.");
            return thumbnailSvc.generateImage(prompt)
                    .thenApply(imageId -> {
                        return ResponseEntity.badRequest().build();
                    });
        }
    }

    @Operation(summary = "썸네일 요청", description = "썸네일 생성 요청을 통해 생성된 썸네일을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = Resource.class))),
            @ApiResponse(responseCode = "400", description = "오류"),
            @ApiResponse(responseCode = "401", description = "사용자가 인증되지 않았습니다."),
            @ApiResponse(responseCode = "404", description = "썸네일을 찾을 수 없습니다.")
    })
    @GetMapping("thumbnail/{id}")
    public ResponseEntity<Resource> getImage(
            @PathVariable(name = "id", required = true) String id,
            HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        try {
            Resource imageResource = thumbnailSvc.getImageAsResource(id);
            logSvc.createLog(0, user, "유저가 생성된 썸네일을 요청했습니다: id=" + id);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + id + ".png\"")
                    .body(imageResource);
        } catch (Exception e) {
            logSvc.createLog(0, user, "생성된 썸네일을 요청했습니다.");
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "썸네일 생성 상태 요청", description = "현재 썸네일의 생성 여부를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "오류"),
            @ApiResponse(responseCode = "401", description = "사용자가 인증되지 않았습니다."),
            @ApiResponse(responseCode = "404", description = "썸네일을 찾을 수 없습니다.")
    })
    @GetMapping("/thumbnail/status/{id}")
    public ResponseEntity<String> getStatus(
            @PathVariable(name = "id", required = true) String id,
            HttpServletRequest request) {
        try {
            // 이미지가 존재하는지 확인하여 상태 반환
            thumbnailSvc.getImageAsResource(id);
            return ResponseEntity.ok("completed");
        } catch (Exception e) {
            return ResponseEntity.ok("processing");
        }
    }

    @Operation(summary = "WAV 파일 변환 요청", description = "요청한 음원 파일을 WAV 형식으로 변환해서 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "오류"),
            @ApiResponse(responseCode = "401", description = "사용자가 인증되지 않았습니다.")
    })
    @PostMapping(value = "/wav", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> convertToWav(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        try {
            byte[] wavData = audioConverterService.convertToWav(file);
            logSvc.createLog(0, user, "유저가 WAV 파일 변환을 요청했습니다");

            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf("audio/wav"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"converted.wav\"")
                    .body(wavData);
        } catch (Exception e) {
            logSvc.createLog(2, user, "WAV 파일 변환이 실패했습니다");
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "맵 파일 변환 요청", description = "요청한 음원 파일을 맵 파일 형식으로 변환해서 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "오류"),
            @ApiResponse(responseCode = "401", description = "사용자가 인증되지 않았습니다.")
    })
    @PostMapping(value = "/map_file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MapFile> convertToMapFile(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        try {
            logSvc.createLog(0, user, "유저가 맵 파일 변환을 요청했습니다");

            List<Double> times = List.of(1.75, 3.02, 4.65, 6.33, 7.49, 9.18, 11.01, 12.62, 14.31, 16.12,
                    17.77, 19.55, 21.24, 23.01, 24.76, 26.44, 28.37, 30.12, 31.93, 33.55,
                    35.41, 37.13, 39.01, 40.77, 42.39, 44.26, 46.17, 47.88, 49.76, 51.48,
                    53.25, 55.03, 56.88, 58.47, 60.26, 62.11, 63.89, 65.63, 67.41, 69.16,
                    70.94, 72.72, 74.56, 76.42, 78.13, 79.81, 81.69, 83.44, 85.17, 86.89,
                    88.66, 90.34, 92.12, 93.81, 95.69, 97.48, 99.16, 100.84, 102.69, 104.53, 106.22,
                    107.98, 109.67, 111.49, 113.23, 115.04, 116.76, 118.41, 120.23, 121.94, 123.66,
                    125.42, 127.13, 128.84, 130.59, 132.27, 134.01, 135.82, 137.56, 139.27, 141.06,
                    142.78, 144.49, 146.23, 148.01, 149.67, 151.44, 153.27, 155.01, 156.72, 158.47,
                    160.28, 162.01, 163.76, 165.55, 167.23, 168.99, 170.82, 172.63, 174.42, 176.23);
            MapFile mapFile = new MapFile(times);

            return ResponseEntity.ok().body(mapFile);
        } catch (Exception e) {
            logSvc.createLog(2, user, "맵 파일 변환이 실패했습니다");
            return ResponseEntity.badRequest().build();
        }
    }

    // @PostMapping("/map_file", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    // public ResponseEntity<MapFile> login(
    // @RequestParam String user_id
    // ) {
    // var response;
    // // 응답 반환
    // return ResponseEntity.ok(response);
    // }
}
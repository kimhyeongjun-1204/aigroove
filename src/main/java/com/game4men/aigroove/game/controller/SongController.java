package com.game4men.aigroove.game.controller;

import com.game4men.aigroove.game.service.ThumbnailSvc;
import com.game4men.aigroove.game.DTO.ImageGenerationResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/game/song")
@Tag(name = "o[개발중]곡 등록 API", description = "곡 등록 관련 API")
public class SongController {

    @Autowired
    private ThumbnailSvc thumbnailSvc;

    @Operation(summary = "썸네일 생성 요청", description = "프롬프트를 통한 썸네일 생성 요청을 서버로 전송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성 성공", content = @Content(schema = @Schema(implementation = ImageGenerationResponse.class))),
            @ApiResponse(responseCode = "400", description = "오류"),
            @ApiResponse(responseCode = "401", description = "사용자가 인증되지 않았습니다.")
    })
    @PostMapping("/thumbnail/generate")
    public CompletableFuture<ResponseEntity<ImageGenerationResponse>> generateImage(
            @RequestParam(name = "prompt", required = true) String prompt,
            HttpServletRequest request) {
        return thumbnailSvc.generateImage(prompt)
                .thenApply(imageId -> {
                    String imageUrl = "./generated_images/" + imageId;
                    return ResponseEntity
                            .ok(new ImageGenerationResponse(imageId, imageUrl, "Image generation completed"));
                });
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
        try {
            Resource imageResource = thumbnailSvc.getImageAsResource(id);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + id + ".png\"")
                    .body(imageResource);
        } catch (Exception e) {
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

    // @PostMapping("/map_file", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    // public ResponseEntity<MapFile> login(
    // @RequestParam String user_id
    // ) {
    // var response;
    // // 응답 반환
    // return ResponseEntity.ok(response);
    // }
}
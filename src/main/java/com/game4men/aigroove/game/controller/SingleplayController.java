package com.game4men.aigroove.game.controller;

import com.game4men.aigroove.common.entity.User;
import com.game4men.aigroove.game.DTO.BadgeDTO;
import com.game4men.aigroove.game.DTO.GameRoomDTO;
import com.game4men.aigroove.game.DTO.PlayResultDTO;
import com.game4men.aigroove.game.DTO.PlayStatusDTO;
import com.game4men.aigroove.game.service.BadgeSvc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game/play/single")
@Tag(name = "싱글플레이 API", description = "싱글플레이 관련 API")
public class SingleplayController {
    @Autowired
    private BadgeSvc badgeSvc;

    @Operation(summary = "게임 결과 업로드하기", description = "게임 결과를 서버에 업로드합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = PlayResultDTO.class))),
            @ApiResponse(responseCode = "400", description = "오류")
    })
    @PostMapping("/result")
    public ResponseEntity<Void> addPlayResult(
            @RequestBody PlayResultDTO playResult,
            HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        List<BadgeDTO> dto = playResult.getAchieved_badges();
        badgeSvc.updateBadgeStatus(dto, user);
        return ResponseEntity.ok().build();
    }
}
package com.game4men.aigroove.game.controller;
import com.game4men.aigroove.common.entity.User;
import com.game4men.aigroove.game.DTO.BadgeDTO;
import com.game4men.aigroove.game.DTO.JwtResponse;
import com.game4men.aigroove.game.service.BadgeSvc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
@RequestMapping("/api/game/badge")
@Tag(name = "뱃지 API", description = "뱃지 관련 API")
public class BadgeController {   
    @Autowired
    private BadgeSvc badgeSvc;

    @Operation(summary = "현재 뱃지 상태 가져오기", description = "badge_id 에 해당하는 뱃지 상태를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = BadgeDTO.class)))),
            @ApiResponse(responseCode = "400", description = "오류")
    })
    @GetMapping("/status/current")
    public ResponseEntity<BadgeDTO> getCurrentBadgeStatus(
        @RequestParam(name = "badge_id", required=true) int badge_id,
        HttpServletRequest request
    ) {
        User user = (User) request.getAttribute("user");
        BadgeDTO badge = badgeSvc.findBadgeByUserAndBadgeCode(user, badge_id);

        return ResponseEntity.ok(badge);
    }
    
    @Operation(summary = "모든 뱃지 상태 가져오기", description = "모든 뱃지 상태를 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = BadgeDTO.class)))),
        @ApiResponse(responseCode = "400", description = "오류")
    })
    @GetMapping("/status/all")
    public ResponseEntity<List<BadgeDTO>> getAllBadgeStatus(
        HttpServletRequest request) 
    {
        User user = (User) request.getAttribute("user");
        List<BadgeDTO> list = badgeSvc.findBadgesByUser(user);

        return ResponseEntity.ok(list);
    }

    @Operation(summary = "뱃지 상태 업데이트", description = "뱃지 리스트를 통해 뱃지 상태를 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "401", description = "패스워드 오류")
    })
    @PutMapping("/status")
    public ResponseEntity<Void> updateBadgeStatus(
        @RequestBody List<BadgeDTO> badgeStatus,
        HttpServletRequest request
    ) {
        User user = (User) request.getAttribute("user");
        badgeSvc.updateBadgeStatus(badgeStatus, user);
        return ResponseEntity.ok().build();
    }
}
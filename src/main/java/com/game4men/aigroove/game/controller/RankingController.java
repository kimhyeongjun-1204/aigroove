package com.game4men.aigroove.game.controller;
import com.game4men.aigroove.game.DTO.BadgeDTO;
import com.game4men.aigroove.game.DTO.RankingDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game/ranking")
@Tag(name = "o[개발중]랭킹 API", description = "랭킹 관련 API")
public class RankingController {

    @Operation(summary = "랭킹 가져오기 [토큰 불필요]", description = "모든 랭킹 정보를 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RankingDTO.class)))),
        @ApiResponse(responseCode = "400", description = "오류")
    })
    @GetMapping()
    public ResponseEntity<List<RankingDTO>> getAllRankings(
    ) {
        List<RankingDTO> list = new ArrayList<>();
        RankingDTO ranking1 = new RankingDTO();
        list.add(ranking1);

        return ResponseEntity.ok(list);
    }
}
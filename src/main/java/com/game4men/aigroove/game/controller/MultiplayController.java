package com.game4men.aigroove.game.controller;

import com.game4men.aigroove.common.entity.User;
import com.game4men.aigroove.game.DTO.GameRoomDTO;
import com.game4men.aigroove.game.DTO.PlayResultDTO;
import com.game4men.aigroove.game.DTO.PlayStatusDTO;
import com.game4men.aigroove.game.exception.DownloadIncompleteException;
import com.game4men.aigroove.game.exception.GameAlreadyStartedException;
import com.game4men.aigroove.game.service.MultiplaySvc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/game/play/multi")
@Tag(name = "멀티플레이 API", description = "멀티플레이 관련 API")
public class MultiplayController {
        @Autowired
        private MultiplaySvc multiplaySvc;

        @Operation(summary = "플레이파일 다운로드", description = "file_key에 해당하는 플레이파일을 byte[]형식으로 반환합니다.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "성공", content = @Content(schema = @Schema(type = "byte[]", description = "플레이 파일"))),
                        @ApiResponse(responseCode = "400", description = "오류")
        })
        @GetMapping("/play_file")
        public ResponseEntity<byte[]> downloadPlayFile(
                        @RequestParam(name = "file_key", required = true) String fileKey,
                        HttpServletRequest request) {
                byte[] playFile = multiplaySvc.getPlayFile(fileKey);
                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileKey + "\"")
                                .body(playFile);
        }

        @Operation(summary = "게임 룸 및 상태 가져오기", description = "room_code 에 해당하는 게임 룸을 반환합니다.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = GameRoomDTO.class))),
                        @ApiResponse(responseCode = "400", description = "오류")
        })
        @GetMapping("/room/status")
        public ResponseEntity<GameRoomDTO> getRoomStatus(
                        @RequestParam(name = "room_code", required = true) String room_code,
                        HttpServletRequest request) {

                GameRoomDTO dto = multiplaySvc.getGameRoomData(room_code);
                return ResponseEntity.ok().body(dto);
        }

        @Operation(summary = "멀티플레이 룸 생성하기", description = "플레이파일을 업로드하고 생성된 멀티플레이 룸 코드를 반환받습니다.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "성공", content = @Content(schema = @Schema(type = "string", description = "룸 코드"))),
                        @ApiResponse(responseCode = "400", description = "오류")
        })
        @PostMapping(value = "/room/play_file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        // @PostMapping(value = "/room/play_file")
        public ResponseEntity<String> createGameRoom(
                        @RequestPart(name = "play_file", required = true) MultipartFile playFile,
                        HttpServletRequest request) {
                try {
                        User user = (User) request.getAttribute("user");
                        String roomCode = multiplaySvc.createGameRoom(user, playFile);
                        return ResponseEntity.status(HttpStatus.CREATED).body(roomCode);
                } catch (Exception e) {
                        System.err.println(e);
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }
        }

        @Operation(summary = "게임 결과 업로드하기", description = "게임 결과를 서버에 업로드합니다.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "성공", content = @Content(schema = @Schema(implementation = PlayResultDTO.class))),
                        @ApiResponse(responseCode = "400", description = "오류")
        })
        @PostMapping("/result")
        public ResponseEntity<Void> addPlayResult(
                        @RequestBody PlayResultDTO playResult,
                        HttpServletRequest request) {
                multiplaySvc.uploadGameResult(playResult);
                return ResponseEntity.ok().build();
        }

        @Operation(summary = "게임 현황 업로드하기", description = "멀티플레이 도중 자신의 게임 현황을 서버에 업로드하고, 상대의 게임 현황을 반환합니다.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "성공", content = @Content(schema = @Schema(implementation = PlayStatusDTO.class))),
                        @ApiResponse(responseCode = "400", description = "오류")
        })
        @PutMapping("/room/game_status")
        public ResponseEntity<PlayStatusDTO> addPlayStatus(
                        @RequestParam(name = "room_code", required = true) String room_code,
                        @RequestBody PlayStatusDTO playStatus,
                        HttpServletRequest request) {
                User user = (User)request.getAttribute("user");
                GameRoomDTO gameRoom = multiplaySvc.getGameRoomData(room_code);
                
                int userId = user.getUser_id();
                int opponentId = (userId == gameRoom.getGuest_id()) ? gameRoom.getHost_id() : gameRoom.getGuest_id();

                multiplaySvc.updatePlayStatus(user, playStatus);

                PlayStatusDTO opponentPlayStatus = multiplaySvc.getPlayStatus(opponentId, room_code);
                return ResponseEntity.ok(opponentPlayStatus);
        }

        @Operation(summary = "게임 룸 상태 갱신하기", description = "게임 룸의 상태를 갱신합니다.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "성공"),
                        @ApiResponse(responseCode = "400", description = "오류"),
                        @ApiResponse(responseCode = "404", description = "유저 존재하지 않음", content = @Content(schema = @Schema(type = "string"))),
        })
        @PutMapping("/room/status")
        public ResponseEntity<?> updateRoomStatus(
                        @RequestParam(name = "room_code", required = true) String room_code,
                        @RequestBody GameRoomDTO gameRoom,
                        HttpServletRequest request) {
                try {
                        multiplaySvc.updateGameRoomData(gameRoom);
                        return ResponseEntity.ok().build();
                } catch (Exception e) {
                        if (e instanceof NoSuchElementException) {
                                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 guest_id 입니다.");
                        } else {
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("요청이 잘못되었습니다.");
                        }
                }
        }

        @Operation(summary = "게임 시작하기", description = "멀티플레이 시 준비가 완료되면 게임 시작을 요청합니다.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "성공"),
                        @ApiResponse(responseCode = "400", description = "오류")
        })
        @PutMapping("/room/game_start")
        public ResponseEntity<?> startGame(
                        @RequestParam(name = "room_code", required = true) String room_code,
                        HttpServletRequest request) {
                try {
                        multiplaySvc.startGame(room_code);
                        return ResponseEntity.ok().build();
                } catch (Exception e) {
                        if (e instanceof NoSuchElementException) {
                                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 guest_id 입니다.");
                        } else if (e instanceof DownloadIncompleteException) {
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                                .body("guest 유저의 다운로드가 완료되지 않았습니다.");
                        } else if (e instanceof GameAlreadyStartedException) {
                                return ResponseEntity.status(HttpStatus.CONFLICT).body("게임이 이미 시작되었습니다.");
                        } else
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("요청이 잘못되었습니다.");
                }
        }
}
package com.game4men.aigroove.game.controller;

import com.game4men.aigroove.game.DTO.UserDTO;
import com.game4men.aigroove.game.service.UserSvc;
import com.game4men.aigroove.game.DTO.LoginRequest;
import com.game4men.aigroove.common.entity.User;
import com.game4men.aigroove.game.DTO.JwtResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/game/user")
@Tag(name = "유저 API", description = "유저 기능 API")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    UserSvc userSvc;

    @Operation(summary = "로그인 [토큰 불필요]", description = "로그인 처리 후 토큰을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "401", description = "패스워드 오류")
    })
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @RequestBody LoginRequest loginRequest) {
        try {
            JwtResponse jwtResponse = userSvc.login(loginRequest);
            return ResponseEntity.ok(jwtResponse);
        } catch (Exception e) {
            System.err.println(e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "회원가입 [토큰 불필요]", description = "회원가입 처리")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "오류"),
            @ApiResponse(responseCode = "409", description = "유저 아이디 중복")
    })
    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @RequestBody UserDTO request) {
        try {
            userSvc.signup(request);
        } catch (Exception e) {
            if (e instanceof DuplicateKeyException){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("중복된 유저 아이디입니다.");
            }
            System.err.println(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "로그아웃", description = "로그아웃 처리 후 서버에 로그를 남깁니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공"),
        @ApiResponse(responseCode = "400", description = "오류")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request) {
        try {
            User user = (User) request.getAttribute("user");
            System.out.println(user);
            // log 남기기
        } catch (Exception e) {
            System.err.println(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "튜토리얼 완료", description = "튜토리얼 완료 정보를 업데이트합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공"),
        @ApiResponse(responseCode = "400", description = "오류")
    })
    @GetMapping("/tutorial_complete")
    public ResponseEntity<Void> complete_tutorial(
            HttpServletRequest request) {
        try {
            User user = (User) request.getAttribute("user");
            userSvc.completeTutorial(user);
        } catch (Exception e) {
            System.err.println(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 처리")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "탈퇴 성공", content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "오류"),
            @ApiResponse(responseCode = "401", description = "사용자가 인증되지 않았습니다.")
    })
    @DeleteMapping("")
    public ResponseEntity<Void> deleteAccount(
            HttpServletRequest request) {
        try {
            User user = (User) request.getAttribute("user");
            userSvc.deleteAccount(user);
        } catch (Exception e) {
            System.err.println(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().build();
    }
}

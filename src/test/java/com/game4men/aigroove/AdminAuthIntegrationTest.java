package com.game4men.aigroove;

import com.game4men.aigroove.common.entity.Admin;
import com.game4men.aigroove.common.entity.User;
import com.game4men.aigroove.common.repository.LoginRepository;
import com.game4men.aigroove.common.repository.UserRepository;
import com.game4men.aigroove.common.utils.JwtUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 관리자 API 인증·인가 통합 테스트.
 *
 * 관리자와 게임 유저에게 같은 username을 부여해, 이름이 겹쳐도
 * 서로의 권한을 얻지 못하는지까지 함께 검증한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminAuthIntegrationTest {

    /** 관리자와 게임 유저가 공유하는 username. 이름 충돌 상황을 만들기 위한 값. */
    private static final String SHARED_USERNAME = "collide";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    private String adminToken;
    private String gameUserToken;

    @BeforeEach
    void setUp() {
        Admin admin = new Admin();
        admin.setUsername(SHARED_USERNAME);
        admin.setHashedPassword(passwordEncoder.encode("adminpw"));
        admin.setName("테스트관리자");
        admin.setBirth(LocalDate.of(1999, 1, 1));
        admin.setSignupDate(LocalDate.now());   // null이면 미승인 상태가 된다
        admin.setRole(Admin.Role.MASTER);
        loginRepository.save(admin);

        User user = new User();
        user.setUsername(SHARED_USERNAME);      // 관리자와 같은 이름
        user.setHashed_password(passwordEncoder.encode("userpw"));
        user.setEmail("collide@test.com");
        user.setNickname("collide");
        userRepository.save(user);

        adminToken = jwtUtils.generateToken(SHARED_USERNAME, "ADMIN");
        gameUserToken = jwtUtils.generateToken(SHARED_USERNAME, "USER");
    }

    @Test
    @DisplayName("토큰 없이 관리자 API를 호출하면 401을 반환한다")
    void returns401_whenTokenIsMissing() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("잘못된 서명 토큰은 거부된다")
    void returns401_whenTokenSignatureIsInvalid() throws Exception {
        // 형식은 JWT지만 서명이 우리 키로 만들어지지 않은 토큰
        String invalidToken = adminToken.substring(0, adminToken.lastIndexOf('.')) + ".WRONG_SIGNATURE";

        mockMvc.perform(get("/admin/dashboard")
                        .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("게임 유저는 관리자 API에 접근을 못한다")
    void returns403_whenGameUserTokenAccessesAdminApi() throws Exception{
        mockMvc.perform(get("/admin/dashboard")
                .header("Authorization", "Bearer " + gameUserToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("관리자는 관리자 API에 접근한다")
    void returns200_whenAdminTokenAccessesAdminApi() throws Exception {
        mockMvc.perform(get("/admin/dashboard")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("토큰 없이 게임 보호 API는 거부된다")
    void returns401_whenGameApiCalledWithoutToken() throws Exception {
        String invalidToken = gameUserToken.substring(0, gameUserToken.lastIndexOf('.')) + ".WRONG_SIGNATURE";

        mockMvc.perform(get("/api/game/badge/status/all")
                .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized());

    }

}

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 관리자 역할별 API 영역 분리(RBAC) 통합 테스트.
 *
 * 의도한 권한표 (프론트 Sidebar.js 메뉴 기준)
 *   /admin/admin/**                                  : MASTER
 *   /admin/user/**, /admin/inquiry/**, /admin/notice/** : MASTER, USER
 *   /admin/ai/**, /admin/dataset/**                   : MASTER, AI
 *   그 외 /admin/**                                   : 모든 관리자
 *
 * 관리자 USER 역할과 게임 유저가 같은 권한 이름을 받지 않는지도 함께 검증한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminRoleAuthorizationTest {

    // AdminAuthIntegrationTest 의 "collide" 와 겹치지 않는 이름을 쓴다.
    private static final String MASTER_USERNAME = "rbac_master";
    private static final String USER_ADMIN_USERNAME = "rbac_user";
    private static final String AI_ADMIN_USERNAME = "rbac_ai";
    private static final String PENDING_USERNAME = "rbac_pending";
    private static final String GAME_USERNAME = "rbac_gamer";

    // 영역별로 고른 부작용 없는 GET 엔드포인트
    private static final String USER_AREA_GET = "/admin/user";          // 사용자 목록
    private static final String AI_AREA_GET = "/admin/dataset/all";     // 데이터셋 목록

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

    private Admin master;
    private Admin userAdmin;
    private Admin pendingAdmin;

    private String masterToken;
    private String userAdminToken;
    private String aiAdminToken;
    private String gameUserToken;

    @BeforeEach
    void setUp() {
        master = saveAdmin(MASTER_USERNAME, Admin.Role.MASTER, LocalDate.now());
        userAdmin = saveAdmin(USER_ADMIN_USERNAME, Admin.Role.USER, LocalDate.now());
        saveAdmin(AI_ADMIN_USERNAME, Admin.Role.AI, LocalDate.now());
        pendingAdmin = saveAdmin(PENDING_USERNAME, null, null);   // 승인 대기 중인 관리자

        User gameUser = new User();
        gameUser.setUsername(GAME_USERNAME);
        gameUser.setHashed_password(passwordEncoder.encode("userpw"));
        gameUser.setEmail("rbac_gamer@test.com");
        gameUser.setNickname(GAME_USERNAME);
        userRepository.save(gameUser);

        masterToken = jwtUtils.generateToken(MASTER_USERNAME, "ADMIN");
        userAdminToken = jwtUtils.generateToken(USER_ADMIN_USERNAME, "ADMIN");
        aiAdminToken = jwtUtils.generateToken(AI_ADMIN_USERNAME, "ADMIN");
        gameUserToken = jwtUtils.generateToken(GAME_USERNAME, "USER");
    }

    private Admin saveAdmin(String username, Admin.Role role, LocalDate signupDate) {
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setHashedPassword(passwordEncoder.encode("adminpw"));
        admin.setName(username);
        admin.setBirth(LocalDate.of(1999, 1, 1));
        admin.setSignupDate(signupDate);   // null이면 미승인 상태가 된다
        admin.setRole(role);
        return loginRepository.save(admin);
    }

    // 승인 API는 login_id 를 admin_id(숫자)로 해석한다 (AdminSvc.approveAdmin).
    // 인가를 통과하면 실제로 승인에 성공하도록 호출자 자신의 admin_id 를 넣는다.

    @Test
    @DisplayName("USER 관리자는 다른 관리자를 승인할 수 없다")
    void returns403_whenUserAdminAcceptsAdmin() throws Exception {
        mockMvc.perform(post("/admin/admin/accept")
                        .header("Authorization", "Bearer " + userAdminToken)
                        .param("admin_id", String.valueOf(pendingAdmin.getAdminId()))
                        .param("role", "USER")
                        .param("login_id", String.valueOf(userAdmin.getAdminId())))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("MASTER 관리자는 다른 관리자를 승인할 수 있다")
    void returns200_whenMasterAcceptsAdmin() throws Exception {
        mockMvc.perform(post("/admin/admin/accept")
                        .header("Authorization", "Bearer " + masterToken)
                        .param("admin_id", String.valueOf(pendingAdmin.getAdminId()))
                        .param("role", "USER")
                        .param("login_id", String.valueOf(master.getAdminId())))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("USER 관리자는 AI 영역 API에 접근할 수 없다")
    void returns403_whenUserAdminAccessesAiArea() throws Exception {
        mockMvc.perform(get(AI_AREA_GET)
                        .header("Authorization", "Bearer " + userAdminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("AI 관리자는 사용자 영역 API에 접근할 수 없다")
    void returns403_whenAiAdminAccessesUserArea() throws Exception {
        mockMvc.perform(get(USER_AREA_GET)
                        .header("Authorization", "Bearer " + aiAdminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("USER 관리자는 사용자 영역 API에 접근한다")
    void returns200_whenUserAdminAccessesUserArea() throws Exception {
        mockMvc.perform(get(USER_AREA_GET)
                        .header("Authorization", "Bearer " + userAdminToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("AI 관리자는 AI 영역 API에 접근한다")
    void returns200_whenAiAdminAccessesAiArea() throws Exception {
        mockMvc.perform(get(AI_AREA_GET)
                        .header("Authorization", "Bearer " + aiAdminToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("게임 유저는 사용자 영역 API에 접근할 수 없다 (관리자 USER 역할과 권한 이름이 겹치지 않아야 한다)")
    void returns403_whenGameUserAccessesUserArea() throws Exception {
        mockMvc.perform(get(USER_AREA_GET)
                        .header("Authorization", "Bearer " + gameUserToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("토큰 없이 사용자 영역 API를 호출하면 401을 반환한다")
    void returns401_whenUserAreaCalledWithoutToken() throws Exception {
        mockMvc.perform(get(USER_AREA_GET))
                .andExpect(status().isUnauthorized());
    }

}

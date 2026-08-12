package com.game4men.aigroove;

import com.game4men.aigroove.common.entity.Admin;
import com.game4men.aigroove.common.repository.LoginRepository;
import com.game4men.aigroove.common.utils.JwtUtils;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 공지사항 입력값 검증 테스트.
 *
 * @Valid 는 Bean Validation 구현체가 클래스패스에 있어야 동작한다.
 * spring-boot-starter-validation 이 빠지면 제약 애노테이션이 조용히 무시되므로,
 * 이 테스트가 그 상황을 잡아내는 역할을 한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class NoticeValidationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private LoginRepository loginRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtils jwtUtils;

    private String adminToken;

    @BeforeEach
    void setUp() {
        Admin admin = new Admin();
        admin.setUsername("validcheck");
        admin.setHashedPassword(passwordEncoder.encode("pw"));
        admin.setName("검증확인");
        admin.setBirth(LocalDate.of(1999, 1, 1));
        admin.setSignupDate(LocalDate.now());
        admin.setRole(Admin.Role.MASTER);
        loginRepository.save(admin);

        adminToken = jwtUtils.generateToken("validcheck", "ADMIN");
    }

    @Test
    @DisplayName("제목이 비어 있으면 400과 검증 메시지를 반환한다")
    void returns400_whenTitleIsBlank() throws Exception {
        mockMvc.perform(put("/admin/notice/1")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"content\":\"본문\",\"admin_id\":\"1\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(Matchers.containsString("Title is required")));
    }
}

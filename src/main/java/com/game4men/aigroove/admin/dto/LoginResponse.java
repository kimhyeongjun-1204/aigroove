package com.game4men.aigroove.admin.dto;

import com.game4men.aigroove.common.entity.Admin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// 여기에 user_id도 주게 변경
public class LoginResponse {
    private int result_code;
    private String login_token;
    private String admin_name;
    private int admin_id;
    private Admin.Role role;
} 
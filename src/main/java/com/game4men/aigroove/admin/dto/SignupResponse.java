package com.game4men.aigroove.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupResponse {
    private int result_code;  // 201: 성공, 409: 중복된 아이디
} 
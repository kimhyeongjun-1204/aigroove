package com.game4men.aigroove.admin.dto;

import com.game4men.aigroove.common.entity.Admin;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class AdminResponse {
    private final String admin_id;
    private final LocalDate signup_date;
    private final String name;
    private final String username;
    private final LocalDate birth;
    private final Admin.Role role;

    public AdminResponse(Admin admin) {
        this.admin_id = String.valueOf(admin.getAdminId());
        this.signup_date = admin.getSignupDate();
        this.name = admin.getName();
        this.username = admin.getUsername();
        this.birth = admin.getBirth();
        this.role = admin.getRole();
    }
} 
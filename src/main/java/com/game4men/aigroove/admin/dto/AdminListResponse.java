package com.game4men.aigroove.admin.dto;

import com.game4men.aigroove.common.entity.Admin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AdminListResponse {
    private int result_code;
    private List<Admin> approvedAdmins;
    private List<Admin> pendingAdmins;
} 
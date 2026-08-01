package com.game4men.aigroove.admin.controller;

import com.game4men.aigroove.admin.dto.DashboardDto;
import com.game4men.aigroove.admin.service.DashboardSvc;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/dashboard")
public class DashboardController {
    private final DashboardSvc dashboardSvc;

    // 대시보드 데이터 반환
    @GetMapping
    public DashboardDto getDashboardData() {
        return dashboardSvc.getDashboardData(); 
    }
}

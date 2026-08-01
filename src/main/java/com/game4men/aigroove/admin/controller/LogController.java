package com.game4men.aigroove.admin.controller;

import com.game4men.aigroove.admin.dto.LogResponseDto;
import com.game4men.aigroove.admin.service.LogSvc;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/logs")
public class LogController {
    private final LogSvc logSvc;

   @GetMapping
   public ResponseEntity<Map<String, Object>> getAllLogs(@RequestParam(name="admin_id") String adminId) {
       try {
            List<LogResponseDto> logs = logSvc.getAllLogs(adminId);
            Map<String, Object> response = new HashMap<>();

            response.put("result_code", 201);
            response.put("logs", logs);
            return ResponseEntity.ok(response);
       }catch (Exception e) {
           Map<String, Object> error = new HashMap<>();
           error.put("result_code", 500);
           error.put("error", e.getMessage());
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
       }
   }
}

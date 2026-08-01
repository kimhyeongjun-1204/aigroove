package com.game4men.aigroove.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.game4men.aigroove.admin.service.ServerStatusSvc;
import java.util.Map;

@RestController
@RequestMapping("/admin/server")
public class ServerStatusController {
    
    @Autowired
    private ServerStatusSvc serverStatusSvc;
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getServerStatus() {
        try {
            Map<String, Object> status = serverStatusSvc.getServerStatus();
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

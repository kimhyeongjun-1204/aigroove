package com.game4men.aigroove.admin.controller;

import com.game4men.aigroove.admin.dto.InquiryResponse;
import com.game4men.aigroove.admin.service.InquirySvc;
import com.game4men.aigroove.admin.service.AdminSvc;
import com.game4men.aigroove.common.entity.Admin;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController("adminInquiryController")
@RequestMapping("/admin/inquiry")
@RequiredArgsConstructor
public class InquiryController {
    private final InquirySvc inquirySvc;
    private final AdminSvc adminService;

    // 문의 목록 조회 및 검색
    @GetMapping({"", "/search"})
    public ResponseEntity<Map<String, Object>> getInquiryList(
            @RequestParam(required = false) String admin_id,
            @RequestParam(required = false) String keyword) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<InquiryResponse> inquiries = inquirySvc.getInquiryList(admin_id, keyword);
            response.put("result_code", 200);
            response.put("success", true);
            response.put("inquirys", inquiries);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("result_code", 500);
            response.put("success", false);
            response.put("message", "문의 목록 조회 중 오류가 발생했습니다.");
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // 문의 상세 조회
    @GetMapping("/{inquiryId}")
    public ResponseEntity<Map<String, Object>> getInquiryDetail(
            @PathVariable Integer inquiryId,
            @RequestParam(required = false) String admin_id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            InquiryResponse inquiry = inquirySvc.getInquiryDetail(inquiryId);
            response.put("result_code", 200);
            response.put("success", true);
            response.put("inquiry", inquiry);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("result_code", 500);
            response.put("success", false);
            response.put("message", "문의 상세 조회 중 오류가 발생했습니다.");
            
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // 답변 등록 및 메일 발송
    @PostMapping("/{inquiryId}/answer")
    public ResponseEntity<Map<String, Object>> answerInquiry(
            @PathVariable Integer inquiryId,
            @RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String adminId = request.get("admin_id");
            String title = request.get("title");
            String content = request.get("content");
            
            Admin author = adminService.findAdmin(Integer.parseInt(adminId));
            if (author == null) {
                response.put("result_code", 400);
                response.put("success", false);
                response.put("message", "관리자를 찾을 수 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            inquirySvc.answerInquiry(inquiryId, title, content, author);
            
            response.put("result_code", 200);
            response.put("success", true);
            response.put("message", "답변이 성공적으로 등록되었습니다.");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("result_code", 404);
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("result_code", 500);
            response.put("success", false);
            response.put("message", "답변 등록 중 오류가 발생했습니다.");
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
}

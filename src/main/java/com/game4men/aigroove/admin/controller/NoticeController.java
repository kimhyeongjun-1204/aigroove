package com.game4men.aigroove.admin.controller;

import com.game4men.aigroove.common.entity.Admin;
import com.game4men.aigroove.common.entity.Notice;
import com.game4men.aigroove.admin.service.AdminSvc;
import com.game4men.aigroove.admin.service.NoticeSvc;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/notice")
public class NoticeController {
    private final NoticeSvc noticeService;
    private final AdminSvc adminService;

    // 공지사항 목록 조회
    @GetMapping
    public ResponseEntity<Map<String, Object>> getNotices() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Notice> notices = noticeService.findAllNotices();
            List<Map<String, Object>> noticeList = notices.stream()
                .map(notice -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("notice_id", notice.getNoticeId());
                    map.put("title", notice.getTitle());
                    map.put("content", notice.getContent());
                    map.put("author_admin_id", notice.getAuthor().getAdminId());
                    map.put("created_at", notice.getCreatedAt());
                    return map;
                })
                .collect(Collectors.toList());
            
            response.put("result_code", 201);
            response.put("notices", noticeList);
        } catch (Exception e) {
            response.put("result_code", 400);
            response.put("message", "Failed to fetch notices: " + e.getMessage());
            response.put("notices", null);
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    // 공지사항 상세 조회
    @GetMapping("/{noticeId}")
    public ResponseEntity<Map<String, Object>> getNotice(@PathVariable Integer noticeId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Notice notice = noticeService.findNotice(noticeId);
            Map<String, Object> noticeData = new HashMap<>();
            noticeData.put("notice_id", notice.getNoticeId());
            noticeData.put("title", notice.getTitle());
            noticeData.put("content", notice.getContent());
            noticeData.put("author_admin_id", notice.getAuthor().getAdminId());
            noticeData.put("created_at", notice.getCreatedAt());

            response.put("result_code", 201);
            response.put("notice", noticeData);
        } catch (Exception e) {
            response.put("result_code", 400);
            response.put("message", "Failed to fetch notice: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    // 공지사항 작성
    @PostMapping
    public ResponseEntity<Map<String, Object>> createNotice(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String adminId = request.get("admin_id");

            String title = request.get("title");
            String content = request.get("content");
            
            if (title == null || title.isEmpty() || content == null || content.isEmpty()) {
                response.put("result_code", 400);
                response.put("message", "Title and content are required");
                return ResponseEntity.badRequest().body(response);
            }

            Admin author = adminService.findAdmin(Integer.parseInt(adminId));
            if (author == null) {
                response.put("result_code", 400);
                response.put("message", "Admin not found");
                return ResponseEntity.badRequest().body(response);
            }

            Notice notice = noticeService.createNotice(title, content, author);

            Map<String, Object> noticeData = new HashMap<>();
            noticeData.put("notice_id", notice.getNoticeId());
            noticeData.put("title", notice.getTitle());
            noticeData.put("content", notice.getContent());
            noticeData.put("author_admin_id", notice.getAuthor().getAdminId());
            noticeData.put("created_at", notice.getCreatedAt());

            response.put("result_code", 200);
            response.put("notice", noticeData);
        } catch (NumberFormatException e) {
            response.put("result_code", 400);
            response.put("message", "Invalid admin ID format");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("result_code", 400);
            response.put("message", "Failed to create notice: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    // 공지사항 수정
    @PutMapping("/{noticeId}")
    public ResponseEntity<Map<String, Object>> updateNotice(
            @PathVariable Integer noticeId,
            @RequestBody @Valid NoticeRequest request,
            BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();
        
        if (bindingResult.hasErrors()) {
            response.put("result_code", 400);
            response.put("message", "Validation failed: " + bindingResult.getFieldError().getDefaultMessage());
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Admin author = adminService.findAdmin(Integer.parseInt(request.getAdmin_id()));
            if (author == null) {
                response.put("result_code", 400);
                response.put("message", "Admin not found");
                return ResponseEntity.badRequest().body(response);
            }

            Notice notice = noticeService.updateNotice(
                    noticeId,
                    request.getTitle(),
                    request.getContent(),
                    author
            );

            Map<String, Object> noticeData = new HashMap<>();
            noticeData.put("notice_id", notice.getNoticeId());
            noticeData.put("title", notice.getTitle());
            noticeData.put("content", notice.getContent());
            noticeData.put("author_admin_id", notice.getAuthor().getAdminId());
            noticeData.put("created_at", notice.getCreatedAt());

            response.put("result_code", 200);
            response.put("notice", noticeData);
        } catch (Exception e) {
            response.put("result_code", 400);
            response.put("message", "Failed to update notice: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    // 공지사항 삭제
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Map<String, Object>> deleteNotice(
            @PathVariable Integer noticeId,
            @RequestParam("admin_id") String adminId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Admin author = adminService.findAdmin(Integer.parseInt(adminId));
            if (author == null) {
                response.put("result_code", 400);
                response.put("message", "Admin not found");
                return ResponseEntity.badRequest().body(response);
            }

            noticeService.deleteNotice(noticeId, author);
            response.put("result_code", 200);
            response.put("message", "Notice deleted successfully");
        } catch (Exception e) {
            response.put("result_code", 400);
            response.put("message", "Failed to delete notice: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    // Request DTO 클래스
    public static class NoticeRequest {
        @NotBlank(message = "Title is required")
        private String title;
        
        @NotBlank(message = "Content is required")
        private String content;
        
        @NotBlank(message = "Admin ID is required")
        private String admin_id;

        // Getters and Setters
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getAdmin_id() {
            return admin_id;
        }

        public void setAdmin_id(String admin_id) {
            this.admin_id = admin_id;
        }
    }
} 
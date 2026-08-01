package com.game4men.aigroove.admin.service;

import com.game4men.aigroove.common.entity.Admin;
import com.game4men.aigroove.common.entity.Notice;
import com.game4men.aigroove.common.entity.Log;
import com.game4men.aigroove.common.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeSvc {
    private final NoticeRepository noticeRepository;
    private final LogSvc logSvc;

    public List<Map<String, Object>> findNoticesByAdminId(Integer adminId) {
        List<Notice> notices = noticeRepository.findByAuthorAdminIdOrderByCreatedAtDesc(adminId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Notice notice : notices) {
            Map<String, Object> noticeMap = new HashMap<>();
            noticeMap.put("notice_id", notice.getNoticeId());
            noticeMap.put("title", notice.getTitle());
            noticeMap.put("author_admin_id", notice.getAuthor().getAdminId());
            noticeMap.put("created_at", notice.getCreatedAt());
            result.add(noticeMap);
        }

        return result;
    }

    // 1. 공지사항 목록 조회 — Fetch Join으로 N+1 문제 해결 (기존: 1+N회 쿼리 → 개선: 1회)
    public List<Notice> findAllNotices() {
        return noticeRepository.findAllWithAuthor();
    }

    // 2. 공지사항 상세 조회
    public Notice findNotice(Integer noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공지사항입니다."));
    }

    public Notice findRecentNotice(){
        return noticeRepository.findTopByOrderByCreatedAtDesc()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공지사항입니다."));
    }

    // 3. 공지사항 작성
    @Transactional
    public Notice createNotice(String title, String content, Admin author) {
        Notice notice = new Notice();
        notice.setTitle(title);
        notice.setContent(content);
        notice.setAuthor(author);
        notice.setCreatedAt(LocalDateTime.now());

        Notice savedNotice = noticeRepository.save(notice);

        // 로그 기록
        logSvc.saveLog(
            "공지사항을 작성하였습니다. (제목: " + title + ")",
            author,
            Log.LogLevel.INFO
        );

        return savedNotice;
    }

    // 4. 공지사항 수정
    @Transactional
    public Notice updateNotice(Integer noticeId, String title, String content, Admin author) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공지사항입니다."));
        
        notice.setTitle(title);
        notice.setContent(content);
        
        Notice updatedNotice = noticeRepository.save(notice);

        // 로그 기록
        logSvc.saveLog(
            "공지사항을 수정하였습니다. (제목: " + title + ")",
            author,
            Log.LogLevel.INFO
        );

        return updatedNotice;
    }

    // 5. 공지사항 삭제
    @Transactional
    public void deleteNotice(Integer noticeId, Admin author) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공지사항입니다."));
        
        String title = notice.getTitle();
        noticeRepository.delete(notice);

        // 로그 기록
        logSvc.saveLog(
            "공지사항을 삭제하였습니다. (제목: " + title + ")",
            author,
            Log.LogLevel.INFO
        );
    }
}



package com.game4men.aigroove.admin.service;

import com.game4men.aigroove.admin.dto.InquiryResponse;
import com.game4men.aigroove.common.entity.Inquiry;
import com.game4men.aigroove.common.entity.Log;
import com.game4men.aigroove.common.entity.Admin;
import com.game4men.aigroove.common.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquirySvc {
    private final InquiryRepository inquiryRepository;
    private final EmailService emailService;
    private final LogSvc logSvc;
    
    // Fetch Join으로 N+1 문제 해결 (기존: 1+N회 쿼리 → 개선: 1회)
    public List<InquiryResponse> getInquiryList(String adminId, String keyword) {
        List<Inquiry> inquiries = inquiryRepository.findAllWithUserAndAdmin();
        
        return inquiries.stream()
                .filter(inquiry -> {
                    if (keyword == null || keyword.trim().isEmpty()) {
                        return true;
                    }
                    String searchKeyword = keyword.toLowerCase();
                    return inquiry.getTitle().toLowerCase().contains(searchKeyword) ||
                           inquiry.getUser().getUsername().toLowerCase().contains(searchKeyword);
                })
                .map(inquiry -> new InquiryResponse(
                        inquiry.getInquiryId(),
                        inquiry.getTitle(),
                        inquiry.getUser().getUsername(),
                        inquiry.getInquiryDate(),
                        inquiry.getAnswered(),
                        inquiry.getContent()
                ))
                .collect(Collectors.toList());
    }

    public InquiryResponse getInquiryDetail(Integer inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의글이 존재하지 않습니다."));

        return new InquiryResponse(
                inquiry.getInquiryId(),
                inquiry.getTitle(),
                inquiry.getUser().getUsername(),
                inquiry.getInquiryDate(),
                inquiry.getAnswered(),
                inquiry.getContent()
        );
    }

    @Transactional
    public void answerInquiry(Integer inquiryId, String answerTitle, String answerContent, Admin author) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 문의글이 존재하지 않습니다."));
        
        // 답변 상태 업데이트
        inquiry.setAnswered(true);
        
        // 이메일 발송
        String userEmail = inquiry.getUser().getEmail();
        emailService.sendInquiryAnswerEmail(userEmail, inquiry.getTitle(), answerTitle, answerContent);
        
        inquiryRepository.save(inquiry);

        // 로그 기록
        logSvc.saveLog(
            "문의글에 답변을 등록하였습니다. (제목: " + inquiry.getTitle() + ")",
            author,
            Log.LogLevel.INFO
        );
    }
}

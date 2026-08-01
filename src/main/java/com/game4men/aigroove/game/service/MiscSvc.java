package com.game4men.aigroove.game.service;

import com.game4men.aigroove.game.DTO.InquiryDTO;

import lombok.RequiredArgsConstructor;

import com.game4men.aigroove.common.entity.User;
import com.game4men.aigroove.common.entity.DailyLog;
import com.game4men.aigroove.common.entity.Inquiry;
import com.game4men.aigroove.common.repository.DailyLogRepository;
import com.game4men.aigroove.common.repository.InquiryRepository;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MiscSvc {
    private final InquiryRepository inquiryRepository;
    private final DailyLogRepository dailyLogRepository;

    // 모든 상품 조회
    public void saveInquiry(User user, InquiryDTO dto) {
        System.err.println(user);
        Inquiry inquiry = new Inquiry();
        inquiry.setUser(user);
        inquiry.setTitle(dto.getTitle());
        inquiry.setContent(dto.getContent());
        inquiry.setAnswered(false);
        inquiry.setInquiryDate(LocalDate.now());

        inquiryRepository.save(inquiry);

        LocalDate today = LocalDate.now();
        // 기존 로그 있는지 확인
        DailyLog log = dailyLogRepository.findByLogDate(today)
                .orElseGet(() -> {
                    DailyLog newLog = new DailyLog();
                    newLog.setLogDate(today);
                    newLog.setDailyUsers(0);
                    newLog.setSongUploads(0);
                    newLog.setInquirys(0);
                    return newLog;
                });
        int inquirys = log.getInquirys();
        log.setInquirys(inquirys + 1);
        dailyLogRepository.save(log);
        return;
    }
}

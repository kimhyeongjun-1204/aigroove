package com.game4men.aigroove.game.service;
import com.game4men.aigroove.game.DTO.InquiryDTO;
import com.game4men.aigroove.common.entity.User;
import com.game4men.aigroove.common.entity.Inquiry;
import com.game4men.aigroove.common.repository.InquiryRepository;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MiscSvc {
    private final InquiryRepository inquiryRepository;
    
    @Autowired
    public MiscSvc(InquiryRepository inquiryRepository) {
        this.inquiryRepository = inquiryRepository;
    }
    
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
        return;
    }
}

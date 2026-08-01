package com.game4men.aigroove.admin.dto;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class InquiryResponse {
    private Integer inquiry_id;
    private String title;
    private String username;
    private LocalDate inquiry_date;
    private Boolean _answered;
    private String content;

    @Builder
    public InquiryResponse(Integer inquiry_id, String title, String username, LocalDate inquiry_date, boolean is_answered, String content) {
        this.inquiry_id = inquiry_id;
        this.title = title;
        this.username = username;
        this.inquiry_date = inquiry_date;
        this._answered = is_answered;
        this.content = content;
    }
} 
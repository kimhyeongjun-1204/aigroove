package com.game4men.aigroove.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModelResponseDto {
    private Integer model_id;
    private String version;
    private LocalDate released_date;
    private Float accuracy;
    private Float f1_score;
    private String key_updates;
    private boolean selected; // 추가
 }

package com.game4men.aigroove.admin.service;

import com.game4men.aigroove.admin.dto.DashboardDto;
import com.game4men.aigroove.admin.dto.LogResponseDto;
import com.game4men.aigroove.common.entity.DailyLog;
import com.game4men.aigroove.common.entity.Log;
import com.game4men.aigroove.common.entity.ModelInfo;
import com.game4men.aigroove.common.entity.User;
import com.game4men.aigroove.common.repository.DailyLogRepository;
import com.game4men.aigroove.common.repository.LogRepository;
import com.game4men.aigroove.common.repository.ModelInfoRepository;
import com.game4men.aigroove.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DashboardSvc {
    // 총 회원수(total_users) , 총 곡 업로드 수  
    private final UserRepository userRepository;

    // 일일 접속자수 , 일일 곡 업로드 수 , 일일 문의글 수
    private final DailyLogRepository dailyLogRepository;

    // Ai 평균 소요시간
    private final ModelInfoRepository modelInfoRepository;

    // 로그 목록
    private final LogRepository logRepository;


    //업로드 곡
   public DashboardDto getDashboardData() {
       DashboardDto dto = new DashboardDto();

       // 총 회원수
       long totalUsers = userRepository.count();
       dto.setTotalUsers((totalUsers));

       // 총 곡 업로드 수 — DB SUM 집계로 최적화 (기존: 전체 User 로드 후 Java 합산 → 개선: 단일 집계 쿼리)
       long totalSongUploads = userRepository.sumUploadedSongCount();
       dto.setTotalSongUploads(totalSongUploads);

       // 일일 접속자 수 , 일일 곡 업로드 수 , 일일 문의글 수
       // 오늘 일자
       LocalDate today = LocalDate.now();
       System.out.println(today);
       Optional<DailyLog> todayLog = dailyLogRepository.findByLogDate(today);
       if (todayLog.isPresent()) {
           DailyLog log = todayLog.get();
           dto.setDailyUsers(log.getDailyUsers());
           dto.setDailySongUploads(log.getSongUploads());
           dto.setDailyInquirys(log.getInquirys());
       }

       // 총 업로드 곡의 수 추가
       

       // 모델 평균 처리 시간
        Optional<ModelInfo> modelInfo = modelInfoRepository.findBySelected(true);
        if (modelInfo.isPresent()) {
            dto.setAverageModelTime(modelInfo.get().getAverage_model_time());
        } else {
            dto.setAverageModelTime(0); // 또는 기본값/에러 처리
        }

        // 로그 데이터 — Fetch Join으로 N+1 문제 해결 (기존: 1+N회 쿼리 → 개선: 1회)
       List<Log> logs = logRepository.findAllWithUsersOrderByLogIdDesc();
       List<LogResponseDto> logDtos = new ArrayList<>();
       
       for (Log log : logs) {
           LogResponseDto logdto = new LogResponseDto();
           logdto.setLog_time(log.getLogTime());
           logdto.setLog_level(log.getLogLevel());
           logdto.setMessage(log.getMessage());
           
           if (log.getAdmin() != null) {
               logdto.set_admin(true);
               logdto.setUsername(log.getAdmin().getUsername());
           } else if (log.getUser() != null) {
               logdto.set_admin(false);
               logdto.setUsername(log.getUser().getUsername());
           } else {
               logdto.set_admin(false);
               logdto.setUsername("알 수 없음");
           }
           
           logDtos.add(logdto);
       }
       
       dto.setLogs(logDtos);

       return dto;
   }


}

package com.game4men.aigroove.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "Log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Log {
    
    public enum LogLevel {
        INFO,
        WARN,
        ERROR,
        DEBUG
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Integer logId;
    
    // 관리자 로그일 경우 adminId, 유저 로그일 경우 userId 사용
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "log_time", nullable = false)
    private LocalDateTime logTime;
    
    @Column(name = "log_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private LogLevel logLevel;
    
    @Column(name = "message", length = 256, nullable = false)
    private String message;
}
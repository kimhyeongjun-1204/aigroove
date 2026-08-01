package com.game4men.aigroove.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "Admin")
public class Admin {
    public enum Role {
        MASTER,
        USER,
        AI
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Integer adminId;

    @Column(name = "username", length = 20, nullable = false)
    private String username;

    @Column(name = "hashed_password", length = 256, nullable = false)
    private String hashedPassword;

    @Column(name = "signup_date")
    private LocalDate signupDate;

    @Column(name = "name", length = 30, nullable = false)
    private String name;

    @Column(name = "birth", nullable = false)
    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 10)
    private Role role;

    // 삭제 여부 
    @Column(name = "is_delete", nullable = true)
    private Boolean isDelete = false;

}
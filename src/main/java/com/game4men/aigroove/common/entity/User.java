package com.game4men.aigroove.common.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(value="user_id")
    private Integer user_id;
    private String username;
    @JsonProperty(value="hashed_password")
    private String hashed_password;
    private String email;
    private String nickname;
    private Boolean is_tutorial_complete = false;
}
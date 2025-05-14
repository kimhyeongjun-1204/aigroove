package com.game4men.aigroove.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "GameStatus")
@Getter @Setter
@NoArgsConstructor
public class GameStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Integer statusId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_code")
    private GameRoom gameRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "current_progress", nullable = false)
    private Float currentProgress = 0f;

    @Column(name = "last_checkpoint", nullable = false)
    private Float lastCheckpoint = 0f;

    @Column(name = "deaths", nullable = false)
    private Integer deaths = 0;

    @Column(name = "has_cleared", nullable = false)
    private Boolean hasCleared = false;
} 
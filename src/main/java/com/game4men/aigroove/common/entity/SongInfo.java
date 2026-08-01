package com.game4men.aigroove.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "SongInfo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SongInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer songId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_room")
    private GameRoom gameRoom;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "artist", nullable = false)
    private String artist;

    @Column(name = "duration", nullable = false)
    private Integer duration;

    @Column(name = "level", nullable = false)
    private Integer level;
}

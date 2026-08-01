package com.game4men.aigroove.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "DefaultSong")
@Getter @Setter
@NoArgsConstructor
public class DefaultSong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "song_id")
    private Long songId;

    @Column(name = "upload_date", nullable = false)
    private LocalDate uploadDate;

    @Column(name = "title", nullable = false, length = 128)
    private String title;

    @Column(name = "duration", nullable = false, length = 8)
    private String duration; // 예: "3:50"

    @Column(name = "size", nullable = false, length = 8)
    private Integer size; // 예: "27MB"
}
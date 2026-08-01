package com.game4men.aigroove.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PlayFile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayFile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_code", nullable = false)
    private GameRoom gameRoom;
    
    @Column(name = "title", length = 50, nullable = false)
    private String title;
    
    @Column(name = "artist", length = 50, nullable = false)
    private String artist;
    
    @Lob
    @Column(name = "thumbnail", nullable = false)
    private byte[] thumbnail;
    
    @Lob
    @Column(name = "music_src", nullable = false)
    private byte[] musicSrc;
    
    @Lob
    @Column(name = "map", nullable = false)
    private byte[] map;
    
    @Column(name = "duration", nullable = false)
    private Integer duration;
    
    @Column(name = "level", nullable = false)
    private Integer level;
}
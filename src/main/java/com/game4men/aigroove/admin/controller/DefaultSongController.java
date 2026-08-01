// 예시: DefaultSongController.java
package com.game4men.aigroove.admin.controller;

import com.game4men.aigroove.common.entity.DefaultSong;
import com.game4men.aigroove.common.repository.DefaultSongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/defaultsongs")
public class DefaultSongController {

    private final DefaultSongRepository defaultSongRepository;

    @GetMapping
    public List<DefaultSong> getAllDefaultSongs() {
        return defaultSongRepository.findAll();
    }
}
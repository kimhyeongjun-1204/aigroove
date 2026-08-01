package com.game4men.aigroove.common.repository;

import com.game4men.aigroove.common.entity.DefaultSong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DefaultSongRepository extends JpaRepository<DefaultSong, Integer> {

}

package com.game4men.aigroove.common.repository;

import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.game4men.aigroove.common.entity.GameRoom;
import com.game4men.aigroove.common.entity.SongInfo;

@Repository
public interface SongInfoRepository extends JpaRepository<SongInfo, Integer> {
    SongInfo findByGameRoom(GameRoom gameRoom);

}

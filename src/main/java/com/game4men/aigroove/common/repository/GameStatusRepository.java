package com.game4men.aigroove.common.repository;

import com.game4men.aigroove.common.entity.GameRoom;
import com.game4men.aigroove.common.entity.GameStatus;
import com.game4men.aigroove.common.entity.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameStatusRepository extends JpaRepository<GameStatus, Integer> {
    Optional<GameStatus> findByUser(User user);
    void deleteAllByGameRoom(GameRoom gameRoom);
} 
package com.game4men.aigroove.common.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.game4men.aigroove.common.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String Username);
    List<User> findByUsernameContainingOrNicknameContainingOrEmailContaining(
            String username, String nickname, String email);

    // 성능 최적화: 전체 User를 메모리에 로드하는 대신 DB에서 SUM 집계 처리
    @Query("SELECT COALESCE(SUM(u.uploadedSongCount), 0) FROM User u")
    Long sumUploadedSongCount();
}
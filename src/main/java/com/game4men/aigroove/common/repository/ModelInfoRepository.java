package com.game4men.aigroove.common.repository;

import com.game4men.aigroove.common.entity.ModelInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModelInfoRepository extends JpaRepository<ModelInfo, Integer> {
    Optional<ModelInfo> findFirstByOrderByReleasedDateDesc();
    List<ModelInfo> findAllByOrderByModelIdDesc();
    Optional<ModelInfo> findBySelected(boolean selected);
    ModelInfo findTopByOrderByModelIdDesc();
} 
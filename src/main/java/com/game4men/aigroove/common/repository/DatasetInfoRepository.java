package com.game4men.aigroove.common.repository;

import com.game4men.aigroove.common.entity.DatasetInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DatasetInfoRepository extends JpaRepository<DatasetInfo, Integer> {
    List<DatasetInfo> findByUploaderAdminIdOrderByUploadDateDesc(Integer adminId);

    // N+1 해결: DatasetInfo 목록 조회 시 Uploader(Admin)를 한 번에 fetch하여 개별 SELECT 방지
    @Query("SELECT d FROM DatasetInfo d LEFT JOIN FETCH d.uploader")
    List<DatasetInfo> findAllWithUploader();
} 
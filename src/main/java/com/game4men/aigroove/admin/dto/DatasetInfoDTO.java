package com.game4men.aigroove.admin.dto;

import com.game4men.aigroove.common.entity.DatasetInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
public class DatasetInfoDTO {
    private Integer datasetId;
    private String uploaderName;  // Admin 객체 대신 이름만 포함
    private String name;
    private LocalDate uploadDate;
    private Float size;
    private String uri;

    public static DatasetInfoDTO fromEntity(DatasetInfo entity) {
        DatasetInfoDTO dto = new DatasetInfoDTO();
        dto.setDatasetId(entity.getDatasetId());
        dto.setUploaderName(entity.getUploader() != null ? entity.getUploader().getName() : null);
        dto.setName(entity.getName());
        dto.setUploadDate(entity.getUploadDate());
        dto.setSize(entity.getSize());
        dto.setUri(entity.getUri());
        return dto;
    }
} 
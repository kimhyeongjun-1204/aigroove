package com.game4men.aigroove.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "DatasetInfo")
@Getter @Setter
@NoArgsConstructor
public class DatasetInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dataset_id")
    private Integer datasetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id")
    private Admin uploader;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "upload_date", nullable = false)
    private LocalDate uploadDate;

    @Column(name = "size", nullable = false)
    private Float size;

    @Column(name = "uri", nullable = false, length = 256)
    private String uri;
} 
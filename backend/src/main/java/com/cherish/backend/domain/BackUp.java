package com.cherish.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BackUp {

    @Id
    @Column(name = "backup_id", unique = true)
    private String id;

    private String osVersion;

    private String deviceType;

    private int diaryCount;

    @Column(name = "created_date",updatable = false)
    private LocalDateTime createdDate;

    @Builder
    private BackUp(String id, String osVersion, String deviceType, int diaryCount) {
        this.id = id;
        this.osVersion = osVersion;
        this.deviceType = deviceType;
        this.diaryCount = diaryCount;
        this.createdDate = LocalDateTime.now();
    }

    public static BackUp of(String id,String osVersion, String deviceType, int diaryCount) {
        return BackUp.builder()
                .id(id)
                .osVersion(osVersion)
                .deviceType(deviceType)
                .diaryCount(diaryCount)
                .build();
    }

}

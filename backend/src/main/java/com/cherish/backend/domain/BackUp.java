package com.cherish.backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BackUp extends BaseEntity {

    @Id
    @Column(name = "backup_id", unique = true)
    private String id;

    private String osVersion;

    private String deviceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avatar_id")
    private Avatar avatar;

    @Builder
    private BackUp(String id, String osVersion, String deviceType, Avatar avatar) {
        this.id = id;
        this.osVersion = osVersion;
        this.deviceType = deviceType;
        this.avatar = avatar;
    }

    public static BackUp of(String osVersion, String deviceType, Avatar avatar) {
        return BackUp.builder()
                .id(UUID.randomUUID().toString().split("-")[0])
                .avatar(avatar)
                .osVersion(osVersion)
                .deviceType(deviceType)
                .build();
    }

    public BackUp renew(String osVersion, String deviceType){
        return BackUp.builder()
                .id(UUID.randomUUID().toString().split("-")[0])
                .avatar(this.avatar)
                .osVersion(osVersion)
                .deviceType(deviceType)
                .build();
    }


}

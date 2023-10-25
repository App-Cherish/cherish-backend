package com.cherish.backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity(name = "session_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SessionToken {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "session_token_id", unique = true, nullable = false)
    private Long id;

    private String sessionTokenVaule;

    private String deviceId;

    private String deviceType;

    private LocalDateTime created_date;

    private LocalDateTime expired_date;

    @ManyToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "avatar_id")
    private Avatar avatar;

    @Column(columnDefinition = "TINYINT(1)")
    private int active;

    public static SessionToken of(String deviceId, String deviceType, Avatar avatar) {
        return SessionToken.builder()
                .sessionTokenVaule(UUID.randomUUID().toString())
                .avatar(avatar)
                .deviceId(deviceId)
                .deviceType(deviceType)
                .created_date(LocalDateTime.now())
                .expired_date(LocalDateTime.now().plusDays(31L))
                .active(1)
                .build();
    }

    @Builder
    private SessionToken(String sessionTokenVaule, String deviceId, String deviceType, LocalDateTime created_date, LocalDateTime expired_date, Avatar avatar, int active) {
        this.sessionTokenVaule = sessionTokenVaule;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.created_date = created_date;
        this.expired_date = expired_date;
        this.avatar = avatar;
        this.active = active;
    }
}

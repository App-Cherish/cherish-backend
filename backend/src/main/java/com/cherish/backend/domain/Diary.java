package com.cherish.backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity(name = "diary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "diary_id", unique = true, nullable = false)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private DiaryKind kind;

    @Column(nullable = true)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime writingDate;

    @Column(nullable = false)
    private String deviceType;

    @Column(nullable = false)
    private String deviceId;



    @ManyToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "avatar_id", nullable = false)
    private Avatar avatar;

    @Builder
    private Diary(DiaryKind kind, String title, String content, LocalDateTime writingDate, String deviceType, String deviceId, Avatar avatar) {
        this.kind = kind;
        this.title = title;
        this.content = content;
        this.writingDate = writingDate;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
        this.avatar = avatar;
    }

    public static Diary of(DiaryKind kind, String title, String content, LocalDateTime writingDate, String deviceType, String deviceId, Avatar avatar) {
        return Diary.builder()
                .avatar(avatar)
                .kind(kind)
                .title(title)
                .content(content)
                .writingDate(writingDate)
                .deviceType(deviceType)
                .deviceId(deviceId)
                .build();
    }

}

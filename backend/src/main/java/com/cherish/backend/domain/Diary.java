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

@Entity(name = "diary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diary extends BaseEntity {

    @Id
    @Column(name = "diary_id", unique = true, nullable = false)
    private String id;

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

    @ManyToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "backup_id")
    private BackUp backUp;

    @Builder
    private Diary(DiaryKind kind, String title, String content, LocalDateTime writingDate, String deviceType, String deviceId, Avatar avatar, BackUp backUp) {
        this.id = createId();
        this.kind = kind;
        this.title = title;
        this.content = content;
        this.writingDate = writingDate;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
        this.avatar = avatar;
        this.backUp = backUp;
    }

    public static Diary of(DiaryKind kind, String title, String content, LocalDateTime writingDate, String deviceType, String deviceId, Avatar avatar,BackUp backUp) {
        return Diary.builder()
                .avatar(avatar)
                .kind(kind)
                .title(title)
                .content(content)
                .writingDate(writingDate)
                .deviceType(deviceType)
                .deviceId(deviceId)
                .backUp(backUp)
                .build();
    }

    public void modifiedBackUp(BackUp changeBackUp) {
        this.backUp = changeBackUp;
    }

    private String createId(){
        return UUID.randomUUID().toString().substring(0,13).replace("-","");
    }

}

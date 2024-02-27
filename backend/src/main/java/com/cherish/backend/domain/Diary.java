package com.cherish.backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Entity(name = "diary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id", unique = true, nullable = false)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private DiaryKind kind;

    @Column
    private String title;

    @Column
    private String content;

    @Column(nullable = false)
    private LocalDateTime clientWritingDate;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "avatar_id", nullable = false)
    private Avatar avatar;

    @ManyToOne(fetch = LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "backup_id")
    private BackUp backUp;


    @Builder
    public Diary(Long id, DiaryKind kind, String title, String content, LocalDateTime clientWritingDate, Avatar avatar, BackUp backUp) {
        this.id = id;
        this.kind = kind;
        this.title = title;
        this.content = content;
        this.clientWritingDate = clientWritingDate;
        this.avatar = avatar;
        this.backUp = backUp;
    }

    public static Diary of(Long id, DiaryKind kind, String title, String content, LocalDateTime clientWritingDate, Avatar avatar, BackUp backUp) {
        return Diary.builder()
                .id(id)
                .avatar(avatar)
                .kind(kind)
                .title(title)
                .content(content)
                .clientWritingDate(clientWritingDate)
                .backUp(backUp)
                .build();
    }

    public static Diary of(DiaryKind kind, String title, String content, LocalDateTime clientWritingDate, Avatar avatar, BackUp backUp) {
        return Diary.builder()
                .avatar(avatar)
                .kind(kind)
                .title(title)
                .content(content)
                .clientWritingDate(clientWritingDate)
                .backUp(backUp)
                .build();
    }


    public void updateBackUp(BackUp backUp) {
        this.backUp = backUp;
    }
}

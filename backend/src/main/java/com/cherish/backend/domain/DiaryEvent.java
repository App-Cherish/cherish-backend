package com.cherish.backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiaryEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "diary_event_id", unique = true, nullable = false)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private DiaryKind kind;

    @Column
    private String title;

    @Column(nullable = false)
    private String content;

    @Column
    @Enumerated(value = EnumType.STRING)
    private DiaryEventType diaryEventType;

    @ManyToOne(fetch = LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "diary_id")
    private Diary diary;

    @ManyToOne(fetch = LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "backup_id")
    private BackUp backUp;

    @Builder
    private DiaryEvent(DiaryKind kind, String title, String content, DiaryEventType diaryEventType, Diary diary, BackUp backUp) {
        this.kind = kind;
        this.title = title;
        this.content = content;
        this.diaryEventType = diaryEventType;
        this.diary = diary;
        this.backUp = backUp;
    }

    public static DiaryEvent of(DiaryKind kind, String title, String content, DiaryEventType diaryEventType, Diary diary, BackUp backUp) {
        return DiaryEvent.builder().
                kind(kind).
                title(title).
                content(content).
                diaryEventType(diaryEventType).
                diary(diary).
                backUp(backUp).
                build();
    }
}

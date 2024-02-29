package com.cherish.backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private String clientId;

    @Column
    private String title;

    @Column
    private String content;

    @Column
    private LocalDateTime clientWritingDate;

    @Column
    @Enumerated(value = EnumType.STRING)
    private DiaryEventType diaryEventType;

    @Column
    private LocalDateTime eventDate;

    @ManyToOne(fetch = LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "backup_id")
    private BackUp backUp;

    @ManyToOne(fetch = LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "avatar_id")
    private Avatar avatar;

    @Builder
    public DiaryEvent(String clientId, DiaryKind kind, String title, String content, LocalDateTime clientWritingDate, DiaryEventType diaryEventType, LocalDateTime eventDate, BackUp backUp, Avatar avatar) {
        this.clientId = clientId;
        this.kind = kind;
        this.title = title;
        this.content = content;
        this.clientWritingDate = clientWritingDate;
        this.diaryEventType = diaryEventType;
        this.eventDate = eventDate;
        this.backUp = backUp;
        this.avatar = avatar;
    }

    public static DiaryEvent of(String clientId, DiaryKind kind, String title, String content, LocalDateTime clientWritingDate, DiaryEventType diaryEventType, LocalDateTime eventDate, BackUp backUp, Avatar avatar) {
        return DiaryEvent.builder().
                clientId(clientId).
                kind(kind).
                clientWritingDate(clientWritingDate).
                title(title).
                content(content).
                diaryEventType(diaryEventType).
                eventDate(eventDate).
                backUp(backUp).
                avatar(avatar).
                build();
    }
}

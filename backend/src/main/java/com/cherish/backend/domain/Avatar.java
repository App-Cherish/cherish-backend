package com.cherish.backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity(name = "avatar")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Avatar extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "avatar_id", unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate birth;

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private Gender gender;

    public static Avatar of(String name, LocalDate birth, Gender gender) {
        return Avatar.builder()
                .name(name)
                .birth(birth)
                .gender(gender)
                .build();
    }

    @Builder
    private Avatar(String name, LocalDate birth, Gender gender) {
        this.name = name;
        this.birth = birth;
        this.gender = gender;
    }
}

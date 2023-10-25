package com.cherish.backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.*;

@Entity(name = "account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseEntity{

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "account_id", unique = true)
    private Long id;

    @Column(name = "oauth_id")
    private String oauthId;

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private Platform platform;

    @ManyToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "avatar_id")
    private Avatar avatar;

    public static Account of(String oauthId,Platform platform, Avatar avatar){
        return Account.builder()
                .oauthId(oauthId)
                .platform(platform)
                .avatar(avatar)
                .build();
    }

    @Builder
    private Account(String oauthId, Platform platform, Avatar avatar) {
        this.oauthId = oauthId;
        this.platform = platform;
        this.avatar = avatar;
    }
}

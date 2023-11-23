package com.cherish.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {


    @CreatedDate
    @CreationTimestamp
    @Column(name = "created_date",updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @UpdateTimestamp
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;


    @Column(columnDefinition = "TINYINT(1)")
    private int active = 1;

    public void deActive() {
        active = 0;
    }

    public void active() {active = 1;}
}

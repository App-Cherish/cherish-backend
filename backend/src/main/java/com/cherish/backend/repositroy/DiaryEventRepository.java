package com.cherish.backend.repositroy;

import com.cherish.backend.domain.DiaryEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryEventRepository extends JpaRepository<DiaryEvent, Long> {
}

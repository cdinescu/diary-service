package com.vitanum.diary.repository;

import com.vitanum.diary.entitities.DiaryEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryEntryRepository extends JpaRepository<DiaryEntry, Integer> {

}

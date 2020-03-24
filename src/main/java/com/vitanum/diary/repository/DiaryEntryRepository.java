package com.vitanum.diary.repository;

import com.vitanum.diary.entitities.DiaryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

public interface DiaryEntryRepository extends JpaRepository<DiaryEntry, Integer> {

    List<DiaryEntry> findByDate(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date);
}

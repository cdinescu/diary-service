package com.vitanum.diary.repository;

import com.vitanum.diary.entitities.DiaryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin
public interface DiaryEntryRepository extends JpaRepository<DiaryEntry, Integer> {

    List<DiaryEntry> findByUsernameAndDate(@RequestParam("username") String username,
                                           @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date);
}

package com.vitanum.diary.controller;

import com.vitanum.diary.entitities.Diary;
import com.vitanum.diary.entitities.DiaryEntry;
import com.vitanum.diary.repository.DiaryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/diaries")
@Slf4j
public class DiaryController {

    @Autowired
    private DiaryRepository diaryRepository;

    @PostMapping(path = "/{year}/{month}/{day}")
    public ResponseEntity<Diary> createDiary(@PathVariable Integer year, @PathVariable Integer month, @PathVariable Integer day) {
        log.info("POST diary (year: {}, month: {}, day: {})", year, month, day);

        LocalDate diaryDate = LocalDate.of(year, month, day);
        Diary diary = new Diary();
        diary.setDate(diaryDate);

        diaryRepository.save(diary);

        return new ResponseEntity<>(diary, HttpStatus.CREATED);
    }

    @GetMapping(path = "/{year}/{month}/{day}")
    public Mono<ResponseEntity<Diary>> getDiary(@PathVariable Integer year, @PathVariable Integer month, @PathVariable Integer day) {
        log.info("GET diary (year: {}, month: {}, day: {})", year, month, day);

        Mono<Diary> diaryMono = getDiaryFromDate(year, month, day);

        return diaryMono.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(new Diary(), HttpStatus.NOT_FOUND));
    }

    @PostMapping(path = "/{year}/{month}/{day}/entries")
    public ResponseEntity<Mono<Diary>> createDiaryEntry(@PathVariable Integer year, @PathVariable Integer month, @PathVariable Integer day, @RequestBody DiaryEntry diaryEntry) {
        log.info("ADD entry {} into diary (year: {}, month: {}, day: {})", diaryEntry, year, month, day);

        Mono<Diary> diaryMono = getDiaryFromDate(year, month, day);
        Diary diary = diaryMono.block();

        if (diary != null) {
            List<DiaryEntry> oldList = diary.getDiaryEntries();

            if (oldList != null) {
                List<DiaryEntry> newList = new ArrayList<>(oldList);
                newList.add(diaryEntry);
                diary.setDiaryEntries(newList);
            }
            return new ResponseEntity<>(diaryRepository.save(diary), HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping(path = "/{year}/{month}/{day}/entries")
    public Mono<ResponseEntity<List<DiaryEntry>>> getDiaryEntries(@PathVariable Integer year, @PathVariable Integer month, @PathVariable Integer day) {
        log.info("GET entries from diary (year: {}, month: {}, day: {})", year, month, day);
        Mono<Diary> diaryMono = getDiaryFromDate(year, month, day);

        return diaryMono.map(diary -> new ResponseEntity<>(diary.getDiaryEntries(), HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST));
    }

    @PutMapping(path = "/{year}/{month}/{day}/entries")
    public Mono<ResponseEntity<Diary>> updateDiaryEntry(@PathVariable Integer year, @PathVariable Integer month, @PathVariable Integer day, @RequestBody DiaryEntry diaryEntry) {
        log.info("UPDATE entry {} into diary (year: {}, month: {}, day: {})", diaryEntry, year, month, day);

        Mono<Diary> diaryMono = getDiaryFromDate(year, month, day);
        return diaryMono.map(result -> this.overwriteEntryAndCreateResponse(result, diaryEntry))
                .defaultIfEmpty(new ResponseEntity<>(new Diary(), HttpStatus.NOT_FOUND));
    }

    private ResponseEntity<Diary> overwriteEntryAndCreateResponse(Diary diary, DiaryEntry diaryEntry) {
        overwriteDiaryEntry(diary, diaryEntry);
        return new ResponseEntity<>(diary, HttpStatus.OK);
    }

    private void overwriteDiaryEntry(Diary diary, DiaryEntry diaryEntry) {
        Optional<DiaryEntry> maybeEntry = diary.getDiaryEntries().stream().filter(entry -> entry.equals(diaryEntry)).findFirst();

        if (!maybeEntry.isPresent()) {
            return;
        }

        DiaryEntry foundEntry = maybeEntry.get();
        foundEntry.setAmount(diaryEntry.getAmount());
        foundEntry.setUnit(diaryEntry.getUnit());
        foundEntry.setCalories(diaryEntry.getCalories());

        // Persist the change in the database
        diaryRepository.save(diary);
    }

    @DeleteMapping(path = "/{year}/{month}/{day}/entries/{diaryEntryTimestamp}")
    public ResponseEntity<Mono<Diary>> deleteDiaryEntry(@PathVariable Integer year, @PathVariable Integer month, @PathVariable Integer day, @PathVariable Long diaryEntryTimestamp) {
        log.info("DELETE entry with unix epoch {} into diary (year: {}, month: {}, day: {})", diaryEntryTimestamp, year, month, day);
        Mono<Diary> diaryMono = getDiaryFromDate(year, month, day);
        Diary diary = diaryMono.block();

        if (diary != null) {
            List<DiaryEntry> oldList = diary.getDiaryEntries();

            if (oldList != null) {
                List<DiaryEntry> newList = new ArrayList<>(oldList);
                newList.stream().filter(entry -> entry.getCreationTimestamp() == diaryEntryTimestamp).forEach(foundEntry -> newList.remove(foundEntry));
                diary.setDiaryEntries(newList);
            }
            return new ResponseEntity<>(diaryRepository.save(diary), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    private Mono<Diary> getDiaryFromDate(Integer year, Integer month, Integer day) {
        Mono<Diary> foundById = diaryRepository.findById(LocalDate.of(year, month, day));
        return (foundById == null) ? Mono.empty() : foundById;
    }
}

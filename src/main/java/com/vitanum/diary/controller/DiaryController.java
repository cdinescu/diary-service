package com.vitanum.diary.controller;

import com.vitanum.diary.entitities.Diary;
import com.vitanum.diary.entitities.DiaryEntry;
import com.vitanum.diary.repository.DiaryEntryRepository;
import com.vitanum.diary.repository.DiaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/diaries")
public class DiaryController {

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private DiaryEntryRepository diaryEntryRepository;

    @PostMapping
    public void createDiary() {
        diaryRepository.save(new Diary(LocalDate.now()));
    }

    @GetMapping(path = "/{diaryId}")
    public ResponseEntity<Diary> getDiary(@PathVariable Integer diaryId) {
        Optional<Diary> diary = diaryRepository.findById(diaryId);

        return diary.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(new Diary(), HttpStatus.NOT_FOUND));
    }

    @PostMapping(path = "/{diaryId}")
    public ResponseEntity<DiaryEntry> createDiaryEntry(@PathVariable Integer diaryId, @RequestBody DiaryEntry diaryEntry) {
        Optional<Diary> diaryOptional = diaryRepository.findById(diaryId);

        if (diaryOptional.isPresent()) {
            Diary diary = diaryOptional.get();
            addEntry(diaryEntry, diary);
            return new ResponseEntity<>(diaryEntry, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new DiaryEntry(), HttpStatus.BAD_REQUEST);
        }
    }

    private void addEntry(@RequestBody DiaryEntry diaryEntry, Diary diary) {
        diaryEntry.setId(0);
        diary.addDiaryEntry(diaryEntry);
        diaryRepository.save(diary);
    }

    @GetMapping(path = "/entries/{diaryEntryId}")
    public ResponseEntity<DiaryEntry> getDiaryEntry(@PathVariable Integer diaryEntryId) {
        Optional<DiaryEntry> diaryEntry = diaryEntryRepository.findById(diaryEntryId);

        return diaryEntry.map(entry -> new ResponseEntity<>(entry, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(new DiaryEntry(), HttpStatus.NOT_FOUND));
    }

    @PutMapping(path = "/{diaryId}")
    public void updateDiaryEntry(@PathVariable Integer diaryId, @RequestBody DiaryEntry diaryEntry) {
        Optional<DiaryEntry> oldDiaryEntry = diaryEntryRepository.findById(diaryEntry.getId());

        oldDiaryEntry.ifPresent(oldEntry -> updatedEntry(oldEntry, diaryEntry));
    }

    private void updatedEntry(DiaryEntry oldEntry, DiaryEntry newEntry) {
        oldEntry.setAmount(newEntry.getAmount());
        oldEntry.setCalories(newEntry.getCalories());
        oldEntry.setUnit(newEntry.getUnit());

        diaryEntryRepository.save(oldEntry);
    }

    @DeleteMapping(path = "/{diaryId}/entries/{diaryEntryId}")
    public void deleteDiaryEntry(@PathVariable Integer diaryId, @PathVariable Integer diaryEntryId) {
        Optional<Diary> diaryOptional = diaryRepository.findById(diaryId);
        Optional<DiaryEntry> diaryEntry = diaryEntryRepository.findById(diaryEntryId);

        if (!diaryOptional.isPresent() || !diaryEntry.isPresent()) {
            return;
        }

        deleteEntry(diaryOptional.get(), diaryEntry.get());
    }

    private void deleteEntry(Diary diary, DiaryEntry diaryEntry) {
        diary.removeDiaryEntry(diaryEntry);

        diaryRepository.save(diary);
    }

}

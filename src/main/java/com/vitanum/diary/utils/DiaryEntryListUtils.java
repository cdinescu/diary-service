package com.vitanum.diary.utils;

import com.vitanum.diary.entitities.Diary;
import com.vitanum.diary.entitities.DiaryEntry;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DiaryEntryListUtils {

    private DiaryEntryListUtils() {

    }

    public static void addEntryIntoDiary(DiaryEntry diaryEntry, Diary diary) {
        log.info("Add entry {} into {}", diaryEntry, diary);
        List<DiaryEntry> oldList = diary.getDiaryEntries();

        if (oldList != null) {
            List<DiaryEntry> newList = new ArrayList<>(oldList);
            newList.add(diaryEntry);
            diary.setDiaryEntries(newList);
        }

        log.info("End result: {}", diary);
    }

    public static void removeEntryFromDiary(Long diaryEntryTimestamp, Diary diary) {
        log.info("Removed entry with timestamp {} from {}", diaryEntryTimestamp, diary);
        List<DiaryEntry> oldList = diary.getDiaryEntries();

        if (oldList != null) {
            List<DiaryEntry> newList = new ArrayList<>(oldList);
            // @formatter:off
            newList.stream()
                    .filter(entry -> entry.getCreationTimestamp() == diaryEntryTimestamp)
                    .forEach(foundEntry -> newList.remove(foundEntry));
            // @formatter:on
            diary.setDiaryEntries(newList);
        }

        log.info("End result: {}", diary);
    }
}

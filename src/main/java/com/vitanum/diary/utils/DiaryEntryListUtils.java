package com.vitanum.diary.utils;

import com.vitanum.diary.entitities.Diary;
import com.vitanum.diary.entitities.DiaryEntry;

import java.util.ArrayList;
import java.util.List;

public class DiaryEntryListUtils {

    private DiaryEntryListUtils() {

    }

    public static void addEntryIntoDiary(DiaryEntry diaryEntry, Diary diary) {
        List<DiaryEntry> oldList = diary.getDiaryEntries();

        if (oldList != null) {
            List<DiaryEntry> newList = new ArrayList<>(oldList);
            newList.add(diaryEntry);
            diary.setDiaryEntries(newList);
        }
    }

    public static void removeEntryFromDiary(Long diaryEntryTimestamp, Diary diary) {
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
    }
}

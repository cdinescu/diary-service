package com.vitanum.diary.utils;

import com.vitanum.diary.entitities.Diary;
import com.vitanum.diary.entitities.DiaryEntry;

import java.time.LocalDate;
import java.time.Month;

public class TestUtils {
    public static final LocalDate DIARY_DATE = LocalDate.of(1991, Month.SEPTEMBER, 9);
    public static final double AMOUNT = 200.0;

    private TestUtils() {

    }

    public static Diary createDiaryForTest() {
        Diary diary = new Diary();
        diary.setDate(DIARY_DATE);

        return diary;
    }

    public static DiaryEntry createDiaryEntryForTest() {
        DiaryEntry newEntry = new DiaryEntry();
        newEntry.setDescription("Tomato");
        newEntry.setCalories(100);
        newEntry.setAmount(AMOUNT);
        newEntry.setUnit("g");

        return newEntry;
    }
}

package com.vitanum.diary.utils;

import com.vitanum.diary.entitities.DiaryEntry;

import java.time.LocalDate;
import java.time.Month;

public class TestUtils {
    public static final LocalDate DIARY_DATE = LocalDate.of(1991, Month.SEPTEMBER, 9);
    public static final double AMOUNT = 200.0;

    private TestUtils() {

    }

    public static DiaryEntry createDiaryEntryForTest() {
        DiaryEntry newEntry = new DiaryEntry();
        newEntry.setDescription("Tomato");
        newEntry.setCalories(100);
        newEntry.setAmount(AMOUNT);
        newEntry.setUnit("g");
        newEntry.setDate(DIARY_DATE);
        newEntry.setUsername("test_username");
        newEntry.setFdcId("09091991");

        return newEntry;
    }
}

package com.vitanum.diary.repository;

import com.vitanum.diary.entitities.Diary;
import com.vitanum.diary.entitities.DiaryEntry;
import com.vitanum.diary.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DiaryRepositoryTest {

    @Autowired
    private DiaryRepository repository;

    public static final LocalDate DIARY_DATE = LocalDate.of(1991, Month.SEPTEMBER, 9);

    @Test
    public void insertDiary() {
        // Arrange
        Diary diary = TestUtils.createDiaryForTest();

        // Act
        Mono<Diary> diaryMono = repository.save(diary);

        // Assert
        StepVerifier.create(diaryMono).assertNext(insertedDiary -> diary.equals(insertedDiary)).expectComplete().verify();
    }

    @Test
    public void insertEntryIntoDiary() {
        // Arrange
        Diary diary = TestUtils.createDiaryForTest();

        // Act
        diary = repository.save(diary).block();
        DiaryEntry diaryEntry = TestUtils.createDiaryEntryForTest();
        diary.setDiaryEntries(Arrays.asList(diaryEntry));

        Mono<Diary> diaryMono = repository.save(diary);

        // Assert
        // formatter:off
        StepVerifier.create(diaryMono)
                .assertNext(updatedDiary -> {
                    assertEquals(1, updatedDiary.getDiaryEntries().size());
                    assertEquals(diaryEntry, updatedDiary.getDiaryEntries().get(0));
                })
                .expectComplete()
                .verify();
        // formatter:on
    }

    @Test
    public void updateEntryFromDiary() {
        // Arrange
        Diary diary = TestUtils.createDiaryForTest();
        DiaryEntry diaryEntry = TestUtils.createDiaryEntryForTest();
        diary.setDiaryEntries(Arrays.asList(diaryEntry));

        // Act
        diary = repository.save(diary).block();
        DiaryEntry updatedEntry = diary.getDiaryEntries().get(0);
        updatedEntry.setAmount(updatedEntry.getAmount() * 2);
        diary.setDiaryEntries(Arrays.asList(updatedEntry));

        Mono<Diary> diaryMono = repository.save(diary);

        // Assert
        // formatter:off
        StepVerifier.create(diaryMono)
                .assertNext(updatedDiary -> {
                    DiaryEntry updateDiaryEntry = updatedDiary.getDiaryEntries().get(0);
                    assertEquals(Double.valueOf(TestUtils.AMOUNT * 2), updateDiaryEntry.getAmount());
                })
                .expectComplete()
                .verify();
        // formatter:on
    }

    @Test
    public void deleteEntryFromDiary() {
        // Arrange
        Diary diary = TestUtils.createDiaryForTest();
        DiaryEntry diaryEntry = TestUtils.createDiaryEntryForTest();
        diary.setDiaryEntries(Arrays.asList(diaryEntry));

        // Act
        diary = repository.save(diary).block();
        diary.setDiaryEntries(new ArrayList<>());
        Mono<Diary> diaryMono = repository.save(diary);

        // Assert
        // formatter:off
        StepVerifier.create(diaryMono)
                .assertNext(updatedDiary -> {
                    assertEquals(0, updatedDiary.getDiaryEntries().size());
                })
                .expectComplete()
                .verify();
        // formatter:on
    }
}

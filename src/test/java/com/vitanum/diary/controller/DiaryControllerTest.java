package com.vitanum.diary.controller;

import com.vitanum.diary.entitities.Diary;
import com.vitanum.diary.entitities.DiaryEntry;
import com.vitanum.diary.repository.DiaryRepository;
import com.vitanum.diary.webtestclient.utils.WebTestClientUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;


/**
 * Test class for DiaryController.
 */
@RunWith(SpringRunner.class)
@WebFluxTest(DiaryController.class)
@DirtiesContext
public class DiaryControllerTest {

    public static final Diary EMPTY_DIARY = new Diary();
    public static final LocalDate DIARY_DATE = LocalDate.of(1991, Month.SEPTEMBER, 9);

    @Autowired
    private WebTestClient client;

    @MockBean
    private DiaryRepository diaryRepository;

    @DisplayName("Create a new diary")
    @Test
    public void createDiary() {
        // Arrange
        Diary diary = createDiaryForTest();

        // Act and Assert
        WebTestClientUtils.postAndVerifyDiary(client, DIARY_DATE, HttpStatus.CREATED);
    }

    @DisplayName("Create a new diary, but already present in DB")
    @Test
    public void createDiaryWhenDiaryAlreadyInDB() {
        // Arrange
        Diary diary = createDiaryForTest();

        when(diaryRepository.findById(DIARY_DATE)).thenReturn(Mono.just(diary));

        // Act and Assert
        WebTestClientUtils.postAndVerifyDiary(client, DIARY_DATE, HttpStatus.CREATED);
    }

    @DisplayName("Read diary.")
    @Test
    public void readDiary_FoundInDatabase() {
        // Arrange
        Diary diary = createDiaryForTest();

        when(diaryRepository.findById(DIARY_DATE)).thenReturn(Mono.just(diary));

        // Act and Assert
        WebTestClientUtils.getAndVerifyDiary(client, DIARY_DATE, HttpStatus.OK, diary);
    }

    @DisplayName("Read a non existing diary")
    @Test
    public void readDiary_NotFoundInDatabase() {
        // Arrange
        Diary diary = createDiaryForTest();

        when(diaryRepository.findById(DIARY_DATE)).thenReturn(Mono.empty());

        // Act and Assert
        WebTestClientUtils.getAndVerifyDiary(client, DIARY_DATE, HttpStatus.NOT_FOUND, EMPTY_DIARY);
    }

    @DisplayName("Read entries from a diary")
    public void readDiaryEntries_DiaryFoundInDatabase() {
        // Arrange
        Diary diary = createDiaryForTest();
        DiaryEntry entry1 = createDiaryEntryForTest();
        DiaryEntry entry2 = createDiaryEntryForTest();

        List<DiaryEntry> entries = new ArrayList<>();
        entries.add(entry1);
        entries.add(entry2);
        diary.setDiaryEntries(entries);

        when(diaryRepository.findById(DIARY_DATE)).thenReturn(Mono.just(diary));

        // Act and Assert
        WebTestClientUtils.getAndVerifyDiaryEntries(client, DIARY_DATE, HttpStatus.OK, diary);
    }

    @DisplayName("Read entries from a non existing diary")
    @Test
    public void readDiaryEntries_DiaryNotFoundInDatabase() {
        // Act and Assert
        WebTestClientUtils.getAndVerifyDiaryEntries(client, DIARY_DATE, HttpStatus.BAD_REQUEST, EMPTY_DIARY);
    }

    @DisplayName("Add entries to a diary")
    @Test
    public void addEntryDiary_DiaryIsFoundInDatabase() {
        // Arrange
        Diary diary = createDiaryForTest();

        DiaryEntry oldEntry = createDiaryEntryForTest();
        long creationTimestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        oldEntry.setCreationTimestamp(creationTimestamp);

        DiaryEntry newEntry = createDiaryEntryForTest();
        newEntry.setCreationTimestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        newEntry.setAmount(oldEntry.getAmount() * 2);

        Diary expectedDiary = diary;
        expectedDiary.setDiaryEntries(Arrays.asList(oldEntry, newEntry));

        when(diaryRepository.findById(DIARY_DATE)).thenReturn(Mono.just(diary));

        // Act and Assert
        WebTestClientUtils.postDiaryEntryAndVerifyDiary(client, DIARY_DATE, HttpStatus.CREATED, newEntry, expectedDiary);
    }

    @DisplayName("Add entries to a non existing diary")
    @Test
    public void addEntryDiary_DiaryIsNotFoundInDatabase() {
        // Arrange
        DiaryEntry newEntry = createDiaryEntryForTest();
        newEntry.setCreationTimestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));

        Diary expectedDiary = EMPTY_DIARY;

        // Act and Assert
        WebTestClientUtils.postDiaryEntryAndVerifyDiary(client, DIARY_DATE, HttpStatus.BAD_REQUEST, newEntry, expectedDiary);
    }

    @DisplayName("Update existing entry")
    @Test
    public void updateEntryDiary_DiaryIsFoundInDatabase_DataEntryInDatabase() {
        // Arrange
        Diary diary = createDiaryForTest();

        DiaryEntry oldEntry = createDiaryEntryForTest();
        long creationTimestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        oldEntry.setCreationTimestamp(creationTimestamp);

        DiaryEntry newEntry = createDiaryEntryForTest();
        newEntry.setCreationTimestamp(creationTimestamp);
        newEntry.setAmount(oldEntry.getAmount() * 2);

        Diary expectedDiary = diary;
        expectedDiary.setDiaryEntries(Arrays.asList(newEntry));

        when(diaryRepository.findById(DIARY_DATE)).thenReturn(Mono.just(diary));

        // Act and Assert
        WebTestClientUtils.putDiaryEntryAndVerifyDiary(client, DIARY_DATE, HttpStatus.OK, newEntry, expectedDiary);
    }

    @DisplayName("Update a non-existing entry")
    @Test
    public void updateEntryDiary_DiaryIsFoundInDatabase_DataEntryNotInDatabase() {
        // Arrange
        LocalDate diaryDate = LocalDate.of(1991, Month.SEPTEMBER, 9);
        Diary diary = new Diary();
        diary.setDate(diaryDate);

        DiaryEntry oldEntry = createDiaryEntryForTest();
        long creationTimestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        oldEntry.setCreationTimestamp(creationTimestamp);

        DiaryEntry newEntry = createDiaryEntryForTest();
        newEntry.setCreationTimestamp(creationTimestamp);
        newEntry.setAmount(oldEntry.getAmount() * 2);

        Diary expectedDiary = diary;

        when(diaryRepository.findById(diaryDate)).thenReturn(Mono.just(diary));

        // Act and Assert
        WebTestClientUtils.putDiaryEntryAndVerifyDiary(client, diaryDate, HttpStatus.OK, newEntry, expectedDiary);
    }

    @DisplayName("Delete existing entry")
    @Test
    public void deleteEntryDiary_DiaryIsFoundInDatabase() {
        // Arrange
        Diary diary = createDiaryForTest();

        DiaryEntry oldEntry = createDiaryEntryForTest();
        long creationTimestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        oldEntry.setCreationTimestamp(creationTimestamp);

        DiaryEntry newEntry = createDiaryEntryForTest();
        newEntry.setCreationTimestamp(creationTimestamp);
        newEntry.setAmount(oldEntry.getAmount() * 2);

        Diary expectedDiary = diary;
        expectedDiary.setDiaryEntries(new ArrayList<>());

        when(diaryRepository.findById(DIARY_DATE)).thenReturn(Mono.just(diary));

        // Act and Assert
        WebTestClientUtils.deleteEntryAndVerifyDiary(client, DIARY_DATE, creationTimestamp, HttpStatus.OK, expectedDiary);
    }

    @DisplayName("Delete entry from non existing diary")
    @Test
    public void deleteEntryDiary_DiaryIsNotFoundInDatabase() {
        // Arrange
        long creationTimestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

        // Act and Assert
        WebTestClientUtils.deleteEntryAndVerifyDiary(client, DIARY_DATE, creationTimestamp, HttpStatus.BAD_REQUEST, EMPTY_DIARY);
    }

    private Diary createDiaryForTest() {
        Diary diary = new Diary();
        diary.setDate(DIARY_DATE);

        return diary;
    }

    private DiaryEntry createDiaryEntryForTest() {
        DiaryEntry newEntry = new DiaryEntry();
        newEntry.setDescription("Tomato");
        newEntry.setCalories(100);
        newEntry.setAmount(200.0);
        newEntry.setUnit("g");

        return newEntry;
    }
}

package com.vitanum.diary.controller;

import com.vitanum.diary.entitities.Diary;
import com.vitanum.diary.entitities.DiaryEntry;
import com.vitanum.diary.repository.DiaryEntryRepository;
import com.vitanum.diary.repository.DiaryRepository;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


/**
 * Test class for DiaryController.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class DiaryControllerTest {

    @Autowired
    private WebTestClient client;

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private DiaryEntryRepository diaryEntryRepository;

    @Before
    public void setUp() {
        diaryEntryRepository.deleteAll();
        diaryRepository.deleteAll();
    }

    @Test
    public void createDiary() {
        // Arrange
        Diary diary = new Diary(LocalDate.of(1991, Month.SEPTEMBER, 9));

        // Act and Assert
        postAndVerifyDiary(diary, HttpStatus.OK);
    }

    @Test
    public void readDiary() {
        // Arrange
        Diary diary = new Diary(LocalDate.of(1991, Month.SEPTEMBER, 9));
        Diary savedDiary = diaryRepository.save(diary);

        // Act and Assert
        WebTestClient.BodyContentSpec andVerifyDiary = getAndVerifyDiary(savedDiary.getId(), HttpStatus.OK);
        String expectedJson = String.format("{\"id\":%s,\"version\":0,\"diaryEntries\":[],\"date\":\"1991-09-09\"}", savedDiary.getId());
        andVerifyDiary.json(expectedJson);
    }

    @Test
    public void readDiaryWithIncorrectId() {
        // Act and Assert
        getAndVerifyDiary(999999, HttpStatus.NOT_FOUND);
    }

    @Test
    public void createDiaryEntry() {
        // Arrange
        Diary diary = new Diary(LocalDate.of(1991, Month.SEPTEMBER, 9));
        Diary savedDiary = diaryRepository.save(diary);

        // Act and Assert
        postAndVerifyDiaryEntry(savedDiary.getId(), HttpStatus.OK);
    }

    @Test
    public void readDiaryEntry() {
        // Arrange
        Diary diary = new Diary(LocalDate.of(1991, Month.SEPTEMBER, 9));

        DiaryEntry diaryEntry = new DiaryEntry();
        diaryEntry.setAmount(100.0);
        diaryEntry.setCalories(50);
        diaryEntry.setUnit("g");
        diaryEntry.setDescription("Banana");

        diary.addDiaryEntry(diaryEntry);
        Diary savedDiary = diaryRepository.save(diary);

        // Act & Assert
        Integer savedEntryId = savedDiary.getDiaryEntries().iterator().next().getId();
        getAndVerifyDiaryEntry(savedEntryId, HttpStatus.OK);
    }

    @Test
    public void readNonExistingDiaryEntry() {
        getAndVerifyDiaryEntry(99999, HttpStatus.NOT_FOUND);
    }

    @Test
    public void createDiaryEntryInNonExistingDiary() {
        // Act and Assert
        postAndVerifyDiaryEntry(9999, HttpStatus.BAD_REQUEST);
    }

    @Test
    public void updateDiaryEntry() {
        // Arrange
        Diary diary = new Diary(LocalDate.of(1991, Month.SEPTEMBER, 9));

        DiaryEntry diaryEntry = new DiaryEntry();
        diaryEntry.setAmount(100.0);
        diaryEntry.setCalories(50);
        diaryEntry.setUnit("g");
        diaryEntry.setDescription("Banana");

        diary.addDiaryEntry(diaryEntry);
        Diary savedDiary = diaryRepository.save(diary);

        // Act
        DiaryEntry newDiaryEntry = savedDiary.getDiaryEntries().iterator().next();
        double newAmount = 200.0;
        newDiaryEntry.setAmount(newAmount);

        // Assert
        putAndVerifyDiaryEntry(savedDiary.getId(), newDiaryEntry, HttpStatus.OK);
        assertEquals(newAmount, savedDiary.getDiaryEntries().iterator().next().getAmount());
    }


    @Test
    public void deleteDiaryEntry() {
        // Arrange
        Diary diary = new Diary(LocalDate.of(1991, Month.SEPTEMBER, 9));

        DiaryEntry diaryEntry = new DiaryEntry();
        diaryEntry.setAmount(100.0);
        diaryEntry.setCalories(50);
        diaryEntry.setUnit("g");
        diaryEntry.setDescription("Banana");

        diary.addDiaryEntry(diaryEntry);
        Diary savedDiary = diaryRepository.save(diary);

        // Act && Assert
        deleteDiaryEntry(savedDiary.getId(), savedDiary.getDiaryEntries().iterator().next().getId());
    }

    private void postAndVerifyDiary(Diary diary, HttpStatus expectedStatus) {
        client.post()
                .uri("/diaries")
                .exchange()
                .expectStatus().isEqualTo(expectedStatus);
    }

    @Test
    public void deleteNonExistingDiaryEntry() {
        // Arrange
        Diary diary = new Diary(LocalDate.of(1991, Month.SEPTEMBER, 9));
        Diary savedDiary = diaryRepository.save(diary);

        // Act && Assert
        deleteDiaryEntry(savedDiary.getId(), 9999);
    }

    private WebTestClient.BodyContentSpec getAndVerifyDiary(int diaryId, HttpStatus expectedStatus) {
        return client.get()
                .uri("/diaries/" + diaryId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody();
    }

    private void postAndVerifyDiaryEntry(Integer diaryId, HttpStatus expectedStatus) {
        client.post()
                .uri("/diaries/" + diaryId)
                .bodyValue(new DiaryEntry("Tomato", 100.0, "g", 50))
                .exchange()
                .expectStatus().isEqualTo(expectedStatus);
    }

    private WebTestClient.BodyContentSpec getAndVerifyDiaryEntry(int diaryEntryId, HttpStatus expectedStatus) {
        return client.get()
                .uri("/diaries/entries/" + diaryEntryId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody();
    }

    private void putAndVerifyDiaryEntry(Integer diaryId, DiaryEntry newDiaryEntry, HttpStatus expectedStatus) {
        client.put()
                .uri("/diaries/" + diaryId)
                .bodyValue(newDiaryEntry)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus);
    }

    private void deleteDiaryEntry(Integer diaryId, Integer diaryEntryId) {
        client.delete()
                .uri("/diaries/" + diaryId + "/entries/" + diaryEntryId)
                .exchange().expectStatus().isEqualTo(HttpStatus.OK);
    }
}

package com.vitanum.diary.webtestclient.utils;

import com.vitanum.diary.entitities.Diary;
import com.vitanum.diary.entitities.DiaryEntry;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.List;

public class WebTestClientUtils {
    private static final String BASE_URL = "/diaries/";

    private WebTestClientUtils() {

    }

    public static void postAndVerifyDiary(WebTestClient client, LocalDate diaryDate, HttpStatus expectedStatus) {
        client.post()
                .uri(constructUrlFromBaseUrlAndDate(diaryDate))
                .exchange()
                .expectStatus().isEqualTo(expectedStatus);
    }

    public static void getAndVerifyDiary(WebTestClient client, LocalDate diaryDate, HttpStatus expectedStatus, Diary expectedResult) {
        client.get()
                .uri(constructUrlFromBaseUrlAndDate(diaryDate))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Diary.class)
                .value(diary -> expectedResult.equals(diary));
    }

    public static void getAndVerifyDiaryEntries(WebTestClient client, LocalDate diaryDate, HttpStatus expectedStatus, Diary expectedResult) {
        client.get()
                .uri(constructUrlFromBaseUrlAndDate(diaryDate) + "/entries")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(List.class)
                .value(result -> expectedResult.getDiaryEntries().equals(result));
    }

    public static void postDiaryEntryAndVerifyDiary(WebTestClient client, LocalDate diaryDate, HttpStatus expectedStatus, DiaryEntry newDiaryEntry, Diary expectedResult) {
        client.post()
                .uri(constructUrlFromBaseUrlAndDate(diaryDate) + "/entries")
                .bodyValue(newDiaryEntry)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody(Diary.class)
                .value(diary -> expectedResult.equals(diary));
    }

    public static void putDiaryEntryAndVerifyDiary(WebTestClient client, LocalDate diaryDate, HttpStatus expectedStatus, DiaryEntry newDiaryEntry, Diary expectedResult) {
        client.put()
                .uri(constructUrlFromBaseUrlAndDate(diaryDate) + "/entries")
                .bodyValue(newDiaryEntry)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody(Diary.class)
                .value(diary -> expectedResult.equals(diary));
    }

    public static void deleteEntryAndVerifyDiary(WebTestClient client, LocalDate diaryDate, long creationTimestamp, HttpStatus expectedStatus, Diary expectedResult) {
        client.delete()
                .uri(constructUrlFromBaseUrlAndDate(diaryDate) + "/entries/" + creationTimestamp)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody(Diary.class)
                .value(diary -> expectedResult.equals(diary));
    }


    private static String constructUrlFromBaseUrlAndDate(LocalDate date) {
        return BASE_URL + date.getYear() + "/" + date.getMonth().getValue() + "/" + date.getDayOfMonth();
    }
}

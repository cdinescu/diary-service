package com.vitanum.diary.webtestclient.utils;

import com.vitanum.diary.entitities.DiaryEntry;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;

public class WebTestClientUtils {
    public static final String USERNAME = "cristina";

    public static final String BASE_URI = "/api/diaryEntries";

    public static final String SEARCH_URI = "/api/diaryEntries/search/findByUsernameAndDate";

    private WebTestClientUtils() {

    }

    public static void postAndVerifyDiaryEntry(WebTestClient client, HttpStatus expectedStatus, DiaryEntry diaryEntry) {
        client.post()
                .uri(BASE_URI)
                .bodyValue(diaryEntry)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus);
    }

    public static WebTestClient.BodyContentSpec getAndVerifyDiaryEntry(WebTestClient client, int diaryEntryId, HttpStatus expectedStatus) {
        return client.get()
                .uri(BASE_URI + "/" + diaryEntryId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody();
    }

    public static void putAndVerifyDiaryEntry(WebTestClient client, Integer diaryEntryId, DiaryEntry newDiaryEntry, HttpStatus expectedStatus) {
        client.put()
                .uri(BASE_URI + "/" + diaryEntryId)
                .bodyValue(newDiaryEntry)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus);
    }

    public static void deleteDiaryEntry(WebTestClient client, Integer diaryEntryId, HttpStatus expectedStatus) {
        client.delete()
                .uri(BASE_URI + "/" + diaryEntryId)
                .exchange().expectStatus().isEqualTo(expectedStatus);
    }

    public static void getByDateAndVerifyDiaryEntry(WebTestClient client, HttpStatus expectedStatus, LocalDate diaryDate, int matchCount) {
        client.get()
                .uri(SEARCH_URI + "?date=" + diaryDate + "&username=" + USERNAME)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.length()")
                .isEqualTo(matchCount + 1); // the self link is also included
    }
}

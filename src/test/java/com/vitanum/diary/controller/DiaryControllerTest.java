package com.vitanum.diary.controller;

import com.vitanum.diary.entitities.DiaryEntry;
import com.vitanum.diary.repository.DiaryEntryRepository;
import com.vitanum.diary.utils.TestUtils;
import com.vitanum.diary.webtestclient.utils.WebTestClientUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;

import static com.vitanum.diary.webtestclient.utils.WebTestClientUtils.USERNAME;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


/**
 * Test class for DiaryController.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DiaryControllerTest {
    @Autowired
    private WebTestClient client;

    @Autowired
    private DiaryEntryRepository diaryEntryRepository;

    @MockBean
    private JwtDecoder jwtDecoder;

    private final Jwt myJwt = jwt("token");

    @BeforeEach
    void setUp() {
        when(this.jwtDecoder.decode(anyString())).thenReturn(this.myJwt);
    }

    private Jwt jwt(String tokenValue) {
        return new Jwt(tokenValue, null, null,
                Map.of("alg", "none"), Map.of("sub", "cristina"));
    }

    @Test
    public void createDiaryUnauthenticated() {
        // Arrange
        DiaryEntry diaryEntry = TestUtils.createDiaryEntryForTest();

        // Act & Arrange
        WebTestClientUtils.postAndVerifyDiaryEntry(client, diaryEntry);
    }

    @Test
    public void readDiaryEntry() {
        // Arrange
        DiaryEntry diaryEntry = TestUtils.createDiaryEntryForTest();
        diaryEntry = diaryEntryRepository.save(diaryEntry);

        // Act  & Assert
        WebTestClientUtils.getAndVerifyDiaryEntry(client, myJwt, diaryEntry.getId(), HttpStatus.OK);
    }

    @Test
    public void readDiaryEntryUnauthenticated() {
        // Arrange
        DiaryEntry diaryEntry = TestUtils.createDiaryEntryForTest();
        diaryEntry = diaryEntryRepository.save(diaryEntry);

        // Act  & Assert
        WebTestClientUtils.getAndVerifyDiaryEntry(client, diaryEntry.getId());
    }

    @Test
    public void searchDiaryEntryByDate() {
        // Arrange
        DiaryEntry diaryEntry1 = TestUtils.createDiaryEntryForTest();
        diaryEntry1.setDate(TestUtils.DIARY_DATE);
        diaryEntry1 = diaryEntryRepository.save(diaryEntry1);
        diaryEntry1.setUsername(USERNAME);

        DiaryEntry diaryEntry2 = TestUtils.createDiaryEntryForTest();
        diaryEntry2 = diaryEntryRepository.save(diaryEntry1);
        diaryEntry2.setDate(TestUtils.DIARY_DATE.plusDays(20));
        diaryEntry2.setUsername(USERNAME);

        // Act  & Assert
        WebTestClientUtils.getByDateAndVerifyDiaryEntry(client, myJwt, HttpStatus.OK, TestUtils.DIARY_DATE, 1);
    }

    @Test
    public void updateDiaryEntry() throws CloneNotSupportedException {
        // Arrange
        DiaryEntry diaryEntry = TestUtils.createDiaryEntryForTest();
        diaryEntry = diaryEntryRepository.save(diaryEntry);

        DiaryEntry updatedDiary = (DiaryEntry) diaryEntry.clone();

        // Act  & Assert
        WebTestClientUtils.putAndVerifyDiaryEntry(client, myJwt, diaryEntry.getId(), updatedDiary, HttpStatus.OK);
    }

    @Test
    public void updateDiaryEntryUnauthenticated() throws CloneNotSupportedException {
        // Arrange
        DiaryEntry diaryEntry = TestUtils.createDiaryEntryForTest();
        diaryEntry = diaryEntryRepository.save(diaryEntry);

        DiaryEntry updatedDiary = (DiaryEntry) diaryEntry.clone();

        // Act  & Assert
        WebTestClientUtils.putAndVerifyDiaryEntry(client, diaryEntry.getId(), updatedDiary);
    }

    @Test
    public void deleteDiaryEntry() {
        // Arrange
        DiaryEntry diaryEntry = TestUtils.createDiaryEntryForTest();
        diaryEntry = diaryEntryRepository.save(diaryEntry);

        // Act & Assert
        WebTestClientUtils.deleteDiaryEntry(client, myJwt, diaryEntry.getId(), HttpStatus.NO_CONTENT);
    }

    @Test
    public void deleteDiaryEntryUnauthenticated() {
        // Arrange
        DiaryEntry diaryEntry = TestUtils.createDiaryEntryForTest();
        diaryEntry = diaryEntryRepository.save(diaryEntry);

        // Act & Assert
        WebTestClientUtils.deleteDiaryEntry(client, diaryEntry.getId());
    }
}

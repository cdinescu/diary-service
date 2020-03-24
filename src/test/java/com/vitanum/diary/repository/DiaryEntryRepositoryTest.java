package com.vitanum.diary.repository;

import com.vitanum.diary.entitities.DiaryEntry;
import com.vitanum.diary.utils.TestUtils;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

/**
 * Test class for the  DiaryEntryRepository.
 */
@RunWith(SpringRunner.class)
@Transactional(propagation = NOT_SUPPORTED)
@DataJpaTest
public class DiaryEntryRepositoryTest {
    @Autowired
    private DiaryEntryRepository diaryEntryRepository;

    @Before
    public void setUp() {
        diaryEntryRepository.deleteAll();
    }

    @Test
    public void create() {
        // Arrange
        DiaryEntry diaryEntry = createDiaryEntry();

        // Act
        DiaryEntry savedEntity = diaryEntryRepository.save(diaryEntry);

        // Assert
        assertDiaryEntryEquals(diaryEntry, savedEntity);
    }

    @Test
    public void readById() {
        // Arrange
        DiaryEntry diaryEntry = createDiaryEntry();
        diaryEntryRepository.save(diaryEntry);

        // Act
        Optional<DiaryEntry> foundDiaryEntryById = diaryEntryRepository.findById(diaryEntry.getId());

        // Assert
        assertTrue(foundDiaryEntryById.isPresent());
        assertDiaryEntryEquals(diaryEntry, foundDiaryEntryById.get());
    }

    @Test
    public void findByDate() {
        // Arrange
        DiaryEntry diaryEntry1 = createDiaryEntry();
        diaryEntry1.setDate(TestUtils.DIARY_DATE);

        DiaryEntry diaryEntry2 = createDiaryEntry();
        diaryEntry2.setDate(TestUtils.DIARY_DATE);

        DiaryEntry diaryEntry3 = createDiaryEntry();
        diaryEntry3.setDate(LocalDate.of(2020, Month.SEPTEMBER, 9));

        diaryEntryRepository.save(diaryEntry1);
        diaryEntryRepository.save(diaryEntry2);
        diaryEntryRepository.save(diaryEntry3);

        // Act
        List<DiaryEntry> byDate = diaryEntryRepository.findByDate(TestUtils.DIARY_DATE);

        // Assert
        assertEquals(2, byDate.size());
    }

    public void update() {
        // Arrange
        DiaryEntry diaryEntry = createDiaryEntry();
        diaryEntryRepository.save(diaryEntry);

        diaryEntry.setAmount(200.0);
        diaryEntryRepository.save(diaryEntry);

        // Act
        Optional<DiaryEntry> foundDiaryEntryById = diaryEntryRepository.findById(diaryEntry.getId());

        // Assert
        assertTrue(foundDiaryEntryById.isPresent());
        DiaryEntry actualDiaryEntry = foundDiaryEntryById.get();

        assertEquals(1, actualDiaryEntry.getVersion());
        assertEquals(200.0, actualDiaryEntry.getAmount());
        assertDiaryEntryEquals(diaryEntry, actualDiaryEntry);
    }

    @Test
    public void delete() {
        // Arrange
        DiaryEntry diaryEntry = createDiaryEntry();
        diaryEntryRepository.save(diaryEntry);

        // Act
        diaryEntryRepository.delete(diaryEntry);

        // Assert
        Optional<DiaryEntry> foundDiaryEntryById = diaryEntryRepository.findById(diaryEntry.getId());
        assertFalse(foundDiaryEntryById.isPresent());
    }

    @Test
    public void optimisticLockError() {
        DiaryEntry diaryEntry = createDiaryEntry();
        DiaryEntry savedEntity = diaryEntryRepository.save(diaryEntry);

        // Store the saved entity in two separate entity objects
        DiaryEntry entity1 = diaryEntryRepository.findById(savedEntity.getId()).get();
        DiaryEntry entity2 = diaryEntryRepository.findById(savedEntity.getId()).get();

        // Update the entity using the first entity object
        entity1.setAmount(200.0);
        diaryEntryRepository.save(entity1);

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds a old version number, i.e. a Optimistic Lock Error
        try {
            entity2.setAmount(500.0);
            diaryEntryRepository.save(entity2);

            fail("Expected an OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException e) {
        }

        // Get the updated entity from the database and verify its new sate
        DiaryEntry updatedEntity = diaryEntryRepository.findById(savedEntity.getId()).get();
        assertEquals(1, updatedEntity.getVersion());
        assertEquals(200.0, updatedEntity.getAmount());
    }

    private DiaryEntry createDiaryEntry() {
        DiaryEntry diaryEntry = new DiaryEntry();
        diaryEntry.setUnit("g");
        diaryEntry.setCalories(100);
        diaryEntry.setAmount(50.0);
        diaryEntry.setDescription("Tomato, raw");

        return diaryEntry;
    }

    private void assertDiaryEntryEquals(DiaryEntry expectedDiaryEntry, DiaryEntry actualDiaryEntry) {
        assertEquals(expectedDiaryEntry.getId(), expectedDiaryEntry.getId());
        assertEquals(expectedDiaryEntry.getAmount(), actualDiaryEntry.getAmount());
        assertEquals(expectedDiaryEntry.getCalories(), actualDiaryEntry.getCalories());
        assertEquals(expectedDiaryEntry.getDescription(), actualDiaryEntry.getDescription());
    }
}

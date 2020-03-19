package com.vitanum.diary.repository;

import com.vitanum.diary.entitities.Diary;
import com.vitanum.diary.entitities.DiaryEntry;
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
import java.util.Iterator;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

/**
 * Test class for the DiaryRepository.
 */
@RunWith(SpringRunner.class)
@Transactional(propagation = NOT_SUPPORTED)
@DataJpaTest
public class DiaryRepositoryTest {
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
    public void create() {
        // Arrange
        Diary diary = new Diary(LocalDate.of(2020, Month.SEPTEMBER, 9));

        // Act
        Diary savedEntity = diaryRepository.save(diary);

        // Assert
        assertDiaryEquals(diary, savedEntity);
    }

    @Test
    public void addDiaryEntry() {
        // Arrange
        Diary diary = new Diary(LocalDate.of(2020, Month.SEPTEMBER, 9));
        DiaryEntry diaryEntry = new DiaryEntry();
        diaryEntry.setAmount(100.0);
        diaryEntry.setCalories(50);
        diaryEntry.setUnit("g");
        diaryEntry.setDescription("Banana");

        diary.addDiaryEntry(diaryEntry);

        // Act
        Diary savedEntity = diaryRepository.save(diary);

        // Assert
        Iterable<DiaryEntry> diaryEntries = diaryEntryRepository.findAll();
        Iterator<DiaryEntry> iterator = diaryEntries.iterator();
        assertTrue(iterator.hasNext());
        assertEquals(1, savedEntity.getDiaryEntries().size());
    }

    @Test
    public void readById() {
        // Arrange
        Diary diary = new Diary(LocalDate.of(2020, Month.SEPTEMBER, 9));
        diaryRepository.save(diary);

        // Act
        Optional<Diary> foundDiaryById = diaryRepository.findById(diary.getId());

        // Assert
        assertTrue(foundDiaryById.isPresent());
        assertDiaryEquals(diary, foundDiaryById.get());
    }

    @Test
    public void update() {
        // Arrange
        Diary diary = new Diary(LocalDate.of(2020, Month.SEPTEMBER, 9));
        LocalDate newDate = LocalDate.of(2019, Month.SEPTEMBER, 9);
        diaryRepository.save(diary);

        // Act
        diary.setDate(newDate);
        diaryRepository.save(diary);

        // Assert
        Optional<Diary> foundDiaryById = diaryRepository.findById(diary.getId());
        assertTrue(foundDiaryById.isPresent());
        Diary actualDiary = foundDiaryById.get();
        assertEquals(1, actualDiary.getVersion());
        assertEquals(newDate, actualDiary.getDate());
        assertDiaryEquals(diary, actualDiary);
    }

    @Test
    public void delete() {
        // Arrange
        Diary diary = new Diary(LocalDate.of(2020, Month.SEPTEMBER, 9));
        diaryRepository.save(diary);

        // Act
        diaryRepository.delete(diary);

        // Assert
        Optional<Diary> foundDiaryById = diaryRepository.findById(diary.getId());
        assertFalse(foundDiaryById.isPresent());
    }

    @Test
    public void optimisticLockError() {
        Diary diary = new Diary(LocalDate.of(2020, Month.SEPTEMBER, 9));
        Diary savedEntity = diaryRepository.save(diary);

        // Store the saved entity in two separate entity objects
        Diary entity1 = diaryRepository.findById(savedEntity.getId()).get();
        Diary entity2 = diaryRepository.findById(savedEntity.getId()).get();

        // Update the entity using the first entity object
        entity1.setDate(LocalDate.of(1991, Month.SEPTEMBER, 9));
        diaryRepository.save(entity1);

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds a old version number, i.e. a Optimistic Lock Error
        try {
            entity2.setDate(LocalDate.of(1991, Month.AUGUST, 9));
            diaryRepository.save(entity2);

            fail("Expected an OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException e) {
            // do nothing
        }

        // Get the updated entity from the database and verify its new sate
        Diary updatedEntity = diaryRepository.findById(savedEntity.getId()).get();
        assertEquals(1, updatedEntity.getVersion());
        assertEquals(LocalDate.of(1991, Month.SEPTEMBER, 9), updatedEntity.getDate());
    }

    private void assertDiaryEquals(Diary expectedDiary, Diary actualDiary) {
        assertEquals(expectedDiary.getId(), actualDiary.getId());
        assertEquals(expectedDiary.getDate(), actualDiary.getDate());
        assertEquals(expectedDiary.getDiaryEntries(), actualDiary.getDiaryEntries());
    }
}

package com.vitanum.diary.repository;

import com.vitanum.diary.entitities.DiaryEntry;
import org.springframework.data.repository.CrudRepository;

public interface DiaryEntryRepository extends CrudRepository<DiaryEntry, Integer> {

}

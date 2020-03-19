package com.vitanum.diary.repository;

import com.vitanum.diary.entitities.Diary;
import org.springframework.data.repository.CrudRepository;

public interface DiaryRepository extends CrudRepository<Diary, Integer> {

}

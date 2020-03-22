package com.vitanum.diary.repository;

import com.vitanum.diary.entitities.Diary;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import java.time.LocalDate;

public interface DiaryRepository extends ReactiveMongoRepository<Diary, LocalDate> {

}

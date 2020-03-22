package com.vitanum.diary.entitities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Document("diaries")
public class Diary {
    @Field
    private LocalDate date;

    @Field("diary_entry")
    private List<DiaryEntry> diaryEntries;

    public Diary() {
        this.diaryEntries = new ArrayList<>();
    }
}

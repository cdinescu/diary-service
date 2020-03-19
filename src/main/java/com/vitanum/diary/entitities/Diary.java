package com.vitanum.diary.entitities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@Table(name = "diaries")
public class Diary {
    @Id
    @GeneratedValue
    private Integer id;

    @Version
    private int version;

    @OneToMany(mappedBy = "diary", cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REMOVE, CascadeType.REFRESH}, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<DiaryEntry> diaryEntries = new HashSet<>();

    private LocalDate date;

    public Diary() {
    }

    public Diary(LocalDate date) {
        this.date = date;
    }

    public void addDiaryEntry(DiaryEntry diaryEntry) {
        this.diaryEntries.add(diaryEntry);
    }

    public void removeDiaryEntry(DiaryEntry diaryEntry) {
        this.diaryEntries.remove(diaryEntry);
    }

}

package com.vitanum.diary.entitities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "diary_entries")
public class DiaryEntry {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "diary_id")
    private Diary diary;

    @Version
    private int version;

    private String description;
    private Double amount;
    private String unit;
    private Integer calories;

    public DiaryEntry() {
    }

    public DiaryEntry(String description, Double amount, String unit, Integer calories) {
        this.description = description;
        this.amount = amount;
        this.unit = unit;
        this.calories = calories;
    }

}

package com.vitanum.diary.entitities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "diary_entries")
public class DiaryEntry implements Cloneable {
    @Id
    @GeneratedValue
    private Integer id;

    @Version
    private int version;

    private String username;
    private LocalDate date;
    private String description;
    private Double amount;
    private String unit;
    private Integer calories;
    private String fdcId;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

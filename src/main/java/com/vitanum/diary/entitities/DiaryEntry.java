package com.vitanum.diary.entitities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
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

    private LocalDate date;
    private String description;
    private Double amount;
    private String unit;
    private Integer calories;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

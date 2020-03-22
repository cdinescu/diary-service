package com.vitanum.diary.entitities;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class DiaryEntry {
    private String description;
    private Double amount;
    private String unit;
    private Integer calories;
    private long creationTimestamp;
}

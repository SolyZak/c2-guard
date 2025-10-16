package com.eden.eden_crm_sec_crm_back.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "task_check_number")
@DiscriminatorValue("number")
public class TaskCheckNumber extends TaskCheck {
    private String unit;
    private String operator;
    private Integer value;
}

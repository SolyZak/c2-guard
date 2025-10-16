package com.eden.eden_crm_sec_crm_back.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "task_check_list")
@DiscriminatorValue("list")
public class TaskCheckList extends TaskCheck{
    private List<String> listItems;
}

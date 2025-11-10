package com.eden.eden_crm_sec_crm_back.models.patrol_execution;

import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "task_patrol_execution")
public class TaskPatrolExecution {
    @Id
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "taskPatrolExecution", cascade = CascadeType.ALL, orphanRemoval = true)
    List<TaskCheckPatrolExecution> taskCheckPatrolExecutions;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference
    private Customer customer;
}

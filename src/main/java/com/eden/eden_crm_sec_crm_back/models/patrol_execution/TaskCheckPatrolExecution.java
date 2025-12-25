package com.eden.eden_crm_sec_crm_back.models.patrol_execution;

import com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "task_check_patrol_execution")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type")
public abstract class TaskCheckPatrolExecution {
    @Id
//    @SequenceGenerator(name = "task_check_patrol_exec_id_seq", sequenceName = "task_check_patrol_exec_id_seq", allocationSize = 1)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_check_patrol_exec_id_seq")
    private Long id;
    private String name;
    private Boolean evidence;
    private Boolean commentCheck;
    private String comment;
    // ✅ Base64-encoded image string (optional)
    private String image;
    @ManyToOne
    @JoinColumn(name = "task_id")
    private TaskPatrolExecution taskPatrolExecution;

    public abstract TaskCheckDTO mapToResponse();
}

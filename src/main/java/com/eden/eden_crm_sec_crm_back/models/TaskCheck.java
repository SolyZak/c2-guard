package com.eden.eden_crm_sec_crm_back.models;

import com.eden.eden_crm_sec_crm_back.dto.request.task.TaskCheckDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "task_check")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type")
public abstract class TaskCheck {
    @Id
    @SequenceGenerator(name = "task_check_id_seq", sequenceName = "task_check_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_check_id_seq")
    private Long id;
    private String name;
    private Boolean evidence;

    @Column(name = "comment_check", nullable = false)
    private Boolean commentCheck = false;

    @ManyToOne

    @JoinColumn(name = "task_id")
    private Task task;

    public abstract TaskCheckDTO mapToResponse();
}

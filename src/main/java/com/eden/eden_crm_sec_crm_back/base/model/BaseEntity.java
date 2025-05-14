package com.eden.eden_crm_sec_crm_back.base.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@MappedSuperclass
@AllArgsConstructor
public class BaseEntity<ID extends Serializable> extends AuditTrail implements Serializable {
    private static final long serialVersionUID = 3855054033844070951L;
    @SequenceGenerator(name = "id_seq", sequenceName = "id_seq", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_seq")
    @Id
    @Schema(description = "The database generated ID")
    private ID id;

    private int isDeleted;

    protected BaseEntity() {
        super();
    }

    protected BaseEntity(ID id) {
        this.id = id;
    }

    protected BaseEntity(ID id, LocalDateTime dateTime) {
        super(dateTime);
        this.id = id;
    }
}

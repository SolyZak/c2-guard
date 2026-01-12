package com.eden.eden_crm_sec_crm_back.entity;

import com.eden.eden_crm_sec_crm_back.enums.CreationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trigger")
@Getter
@Setter
@NoArgsConstructor
public class Trigger {
    @Id
    @SequenceGenerator(name = "trigger_id_seq", sequenceName = "trigger_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trigger_id_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "creation_type")
    @Enumerated(EnumType.STRING)
    private CreationType creationType;

    @Version
    @Column(name = "db_version")
    private Long dbVersion;

}

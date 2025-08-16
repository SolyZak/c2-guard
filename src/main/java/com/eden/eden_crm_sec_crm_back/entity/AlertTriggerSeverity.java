package com.eden.eden_crm_sec_crm_back.entity;

import com.eden.eden_crm_sec_crm_back.enums.Severity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "alert_trigger_severity")
@Getter
@Setter
@NoArgsConstructor
public class AlertTriggerSeverity {
    @Id
    @SequenceGenerator(name = "alertTriggerSeverity_id_seq", sequenceName = "alertTriggerSeverity_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "alertTriggerSeverity_id_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "alert_id")
    private Long alertId;

    @Column(name = "trigger_id")
    private Long triggerId;

    @OneToOne
    @JoinColumn(name = "service_platform_id")
    private ServicePlatform servicePlatform;


    @Column(name = "severity")
    @Enumerated(EnumType.STRING)
    private Severity severity;

    @Version
    @Column(name = "db_version")
    private Long dbVersion;
}

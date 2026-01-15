package com.eden.eden_crm_sec_crm_back.entity;

import com.eden.eden_crm_sec_crm_back.enums.Severity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "alert_trigger_severity", uniqueConstraints = {
        @UniqueConstraint(
            name = "alert_trigger_severity_alert_trigger_id_customer_id_unique",
            columnNames = {"alert_trigger_id", "customer_id"}
        )
})
@Getter
@Setter
@NoArgsConstructor
public class AlertTriggerSeverity {
    @Id
    @SequenceGenerator(name = "alertTriggerSeverity_id_seq", sequenceName = "alertTriggerSeverity_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "alertTriggerSeverity_id_seq")
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "alert_trigger_id")
    private AlertTrigger alertTrigger;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "severity")
    @Enumerated(EnumType.STRING)
    private Severity severity;

    @Version
    @Column(name = "db_version")
    private Long dbVersion;
}

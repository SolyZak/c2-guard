package com.eden.eden_crm_sec_crm_back.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "alert_trigger", uniqueConstraints = {
    @UniqueConstraint(
        name = "alert_trigger_alert_id_trigger_id_service_platform_id_unique",
        columnNames = {"alert_id", "trigger_id", "service_platform_id"}
    )
})
@Getter
@Setter
@NoArgsConstructor
public class AlertTrigger {
    @Id
    @SequenceGenerator(name = "alert_trigger_id_seq", sequenceName = "alert_trigger_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "alert_trigger_id_seq")
    private Long id;

    @Column(name = "alert_id")
    private Long alertId;

    @Column(name = "trigger_id")
    private Long triggerId;

    @ManyToOne
    @JoinColumn(name = "service_platform_id")
    private ServicePlatform servicePlatform;
}

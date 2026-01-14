package com.eden.eden_crm_sec_crm_back.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetTime;

@Entity
@Table(name = "crm_trigger_log")
@Getter
@Setter
@NoArgsConstructor
public class CrmTriggerLog {
    @Id
    @SequenceGenerator(name = "crmTriggerLog_id_seq", sequenceName = "crmTriggerLog_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "crmTriggerLog_id_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "service_trigger_event_id")
    private Long serviceTriggerEventId;

    @Column(name = "trigger_id")
    private Long triggerId;

    @Column(name = "trigger_name")
    private String triggerName;

    @Column(name = "workforce_id")
    private Long workforceId;

    @Column(name = "operation_site_id")
    private Long operationSiteId;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "event_time")
    private OffsetTime eventTime;

    @Column(name = "event_date")
    private LocalDate eventDate;

    @ManyToOne
    @JoinColumn(name = "service_platform_id")
    private ServicePlatform servicePlatform;

    @Column(name = "description")
    private String description;
}

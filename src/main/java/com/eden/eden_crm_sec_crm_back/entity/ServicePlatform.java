package com.eden.eden_crm_sec_crm_back.entity;

import com.eden.eden_crm_sec_crm_back.enums.ServicePlatformEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "service_platform")
@Getter
@Setter
@NoArgsConstructor
public class ServicePlatform {
    @Id
    @SequenceGenerator(name = "service_platform_id_seq", sequenceName = "service_platform_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "service_platform_id_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "name", unique = true)
    @Enumerated(EnumType.STRING)
    private ServicePlatformEnum name;

    @Column(name = "code", unique = true)
    private String code;
}

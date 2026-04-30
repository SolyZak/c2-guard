package com.eden.eden_crm_sec_crm_back.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "operation_site_cameras")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OperationSiteCamera {
    @Id
    @SequenceGenerator(name = "operation_site_cameras_seq", sequenceName = "operation_site_cameras_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "operation_site_cameras_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "camera_id", referencedColumnName = "id", nullable = false)
    private Camera camera;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "operation_site_id", referencedColumnName = "id", nullable = false)
    private CustomerSite operationSite;
}


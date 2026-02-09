package com.eden.eden_crm_sec_crm_back.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "permission_screen")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PermissionScreenEntity extends BaseEntity {

    @Id
    @SequenceGenerator(name = "permission_screen_seq", sequenceName = "permission_screen_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "permission_screen_seq")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "permission_class_id", nullable = false)
    private PermissionClassEntity permissionClass;

    @Column(name = "name_en", length = 100)
    private String nameEn;

    @Column(name = "name_ar", length = 100)
    private String nameAr;

    @OneToMany(mappedBy = "permissionScreen")
    private List<PermissionEntity> permissions = new ArrayList<>();
}
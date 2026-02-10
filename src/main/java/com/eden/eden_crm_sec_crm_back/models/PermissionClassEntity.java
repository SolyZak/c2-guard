package com.eden.eden_crm_sec_crm_back.models;

import com.eden.eden_crm_sec_crm_back.models.PermissionScreenEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "permission_class")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PermissionClassEntity extends BaseEntity {

    @Id
    @SequenceGenerator(name = "permission_class_seq", sequenceName = "permission_class_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "permission_class_seq")
    private Integer id;

    @Column(name = "name_en", length = 100)
    private String nameEn;

    @Column(name = "name_ar", length = 100)
    private String nameAr;

    @OneToMany(mappedBy = "permissionClass")
    private List<PermissionScreenEntity> screens = new ArrayList<>();
}
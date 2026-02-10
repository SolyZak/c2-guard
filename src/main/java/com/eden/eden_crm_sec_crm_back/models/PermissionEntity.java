package com.eden.eden_crm_sec_crm_back.models;
import com.eden.eden_crm_sec_crm_back.models.PermissionScreenEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "permission")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PermissionEntity extends BaseEntity {

    @Id
    @SequenceGenerator(name = "permission_seq", sequenceName = "permission_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "permission_seq")
    private Long id;

    @Column(name = "keycloak_role_name", nullable = false, unique = true)
    private String keycloakRoleName;

    @Column(name = "name_en", length = 100)
    private String nameEn;

    @Column(name = "name_ar", length = 100)
    private String nameAr;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_screen_id")
    private PermissionScreenEntity permissionScreen;

    @JsonIgnore
    @ManyToMany(mappedBy = "permissions")
    private List<RoleEntity> roles = new ArrayList<>();
}
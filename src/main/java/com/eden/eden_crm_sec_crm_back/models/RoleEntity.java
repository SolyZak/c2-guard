package com.eden.eden_crm_sec_crm_back.models;

import com.eden.eden_crm_sec_crm_back.models.BaseEntity;
import com.eden.eden_crm_sec_crm_back.models.PermissionEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "role")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RoleEntity extends BaseEntity {

    @Id
    @SequenceGenerator(name = "role_seq", sequenceName = "role_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq")
    private Integer id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "name", nullable = false, length = 300)
    private String name;

    @Column(name = "keycloak_role_name", nullable = false, length = 400)
    private String keycloakRoleName;

    @Column(name = "description", length = 500)
    private String description;

    @ManyToMany
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private List<PermissionEntity> permissions = new ArrayList<>();
}
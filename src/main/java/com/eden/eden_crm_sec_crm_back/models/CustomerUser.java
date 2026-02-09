package com.eden.eden_crm_sec_crm_back.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "customer_users")
public class CustomerUser extends BaseAuditEntity {

    @Id
    @SequenceGenerator(name = "customer_users_id_seq",
            sequenceName = "customer_users_id_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_users_id_seq")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    @Column(name = "country_code")
    private String countryCode;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_customer"))
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", foreignKey = @ForeignKey(name = "fk_customer_users_role"))
    private RoleEntity role;
}

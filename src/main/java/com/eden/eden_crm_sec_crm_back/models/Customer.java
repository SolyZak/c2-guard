package com.eden.eden_crm_sec_crm_back.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "customers")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE Customer SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false") // Ensures only non-deleted entities are fetched
public class Customer extends BaseEntity {
    @Id
    @SequenceGenerator(name = "customer_seq", sequenceName = "customer_seq", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_seq")
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "country_code")
    private String countryCode = "00966";

    @Column(name = "address")
    private String address;

    @Column(name = "registration_number")
    private String registrationNumber;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean active = false;
}

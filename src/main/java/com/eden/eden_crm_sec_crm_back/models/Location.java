package com.eden.eden_crm_sec_crm_back.models;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "location")
public class Location extends BaseAuditEntity {
    @Id
    @SequenceGenerator(name = "location_seq",
            sequenceName = "location_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "location_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "premise_id", referencedColumnName = "id")
    private Premise premise;
    private String name;
    private String accessType;
    private Double longitude;
    private Double latitude;

    @Lob
    private byte[] qrImage;
}

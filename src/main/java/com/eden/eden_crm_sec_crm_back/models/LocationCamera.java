package com.eden.eden_crm_sec_crm_back.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "location_cameras")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationCamera {

    @Id
    @SequenceGenerator(name = "location_cameras_seq", sequenceName = "location_cameras_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "location_cameras_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", referencedColumnName = "id", nullable = false)
    private Location location;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "camera_id", referencedColumnName = "id", nullable = false)
    private Camera camera;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference
    private Customer customer;
}
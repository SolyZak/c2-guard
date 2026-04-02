package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.LocationCamera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationCameraRepository extends JpaRepository<LocationCamera, Long> {

    @Query("""
            SELECT lc FROM LocationCamera lc
            JOIN FETCH lc.camera c
            JOIN FETCH c.vendor v
            JOIN FETCH lc.customer
            WHERE lc.location.id = :locationId
            """)
    List<LocationCamera> findByLocationIdWithCameraAndVendor(@Param("locationId") Long locationId);

    @Query("SELECT lc.camera.id FROM LocationCamera lc WHERE lc.location.id = :locationId")
    List<Long> findCameraIdsByLocationId(@Param("locationId") Long locationId);
}
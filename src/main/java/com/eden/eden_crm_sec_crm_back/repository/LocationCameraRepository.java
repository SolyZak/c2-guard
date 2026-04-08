package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.LocationCamera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationCameraRepository extends JpaRepository<LocationCamera, Long> {

    @Query("SELECT lc FROM LocationCamera lc JOIN FETCH lc.camera JOIN FETCH lc.camera.vendor WHERE lc.location.id = :locationId AND lc.customer.id = :customerId")
    List<LocationCamera> findByLocationIdAndCustomerIdWithCameraAndVendor(
            @Param("locationId") Long locationId,
            @Param("customerId") Long customerId
    );

    @Query("SELECT lc.camera.id FROM LocationCamera lc WHERE lc.location.id = :locationId AND lc.customer.id = :customerId")
    List<Long> findCameraIdsByLocationIdAndCustomerId(
            @Param("locationId") Long locationId,
            @Param("customerId") Long customerId
    );
}
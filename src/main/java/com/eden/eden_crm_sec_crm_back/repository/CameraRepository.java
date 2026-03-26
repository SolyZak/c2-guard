package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.Camera;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CameraRepository extends JpaRepository<Camera, Long> {

    @Query("SELECT c FROM Camera c JOIN FETCH c.vendor JOIN FETCH c.customer")
    List<Camera> findAllWithVendorAndCustomer();

    @Query(
            value = "SELECT c FROM Camera c JOIN FETCH c.vendor JOIN FETCH c.customer",
            countQuery = "SELECT COUNT(c) FROM Camera c"
    )
    Page<Camera> findAllPaginatedWithVendorAndCustomer(Pageable pageable);

    List<Camera> findByCustomerId(Long customerId);
}
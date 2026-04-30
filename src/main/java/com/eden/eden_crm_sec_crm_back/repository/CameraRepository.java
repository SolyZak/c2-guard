package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.Camera;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CameraRepository extends JpaRepository<Camera, Long> {

    @Query("SELECT c FROM Camera c JOIN FETCH c.vendor JOIN FETCH c.customer WHERE c.customer.id = :customerId")
    List<Camera> findByCustomerIdWithDetails(@Param("customerId") Long customerId);

    @Query(
            value = "SELECT c FROM Camera c JOIN FETCH c.vendor JOIN FETCH c.customer WHERE c.customer.id = :customerId",
            countQuery = "SELECT COUNT(c) FROM Camera c WHERE c.customer.id = :customerId"
    )
    Page<Camera> findByCustomerIdPaginatedWithDetails(@Param("customerId") Long customerId, Pageable pageable);

    Optional<Camera> findByIdAndCustomerId(Long id, Long customerId);

    @Query("SELECT c FROM Camera c JOIN FETCH c.vendor JOIN FETCH c.customer WHERE c.id IN :ids AND c.customer.id = :customerId")
    List<Camera> findAllByIdInAndCustomerId(@Param("ids") List<Long> ids, @Param("customerId") Long customerId);
}
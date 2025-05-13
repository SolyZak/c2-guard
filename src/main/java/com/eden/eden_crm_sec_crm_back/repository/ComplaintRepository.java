package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.ComplaintEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<ComplaintEntity, Integer> {

    @Query("SELECT c FROM ComplaintEntity c WHERE c.deleted = false AND customer.id = :customerId")
    Page<ComplaintEntity> findByCustomer(@Param("customerId") Long customerId, Pageable pageable);

    Optional<ComplaintEntity> findByIdAndCustomerId(Long id, Long customerId);
}

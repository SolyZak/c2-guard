package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {

    @Query("SELECT v FROM Vendor v WHERE LOWER(v.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Vendor> findByNameContaining(@Param("keyword") String keyword, Pageable pageable);

    // findAll() and findAll(Pageable) are already inherited from JpaRepository — no need to redeclare
}
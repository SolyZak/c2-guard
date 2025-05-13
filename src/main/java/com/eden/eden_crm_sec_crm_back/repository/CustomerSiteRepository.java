package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.CustomerSite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerSiteRepository extends JpaRepository<CustomerSite, Long> {

    List<CustomerSite> findByCustomerId(Long customerId);
    Optional<CustomerSite> findByIdAndCustomerId(Long id, Long customerId);

}

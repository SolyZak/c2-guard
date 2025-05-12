package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.CustomerSite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerSiteRepository extends JpaRepository<CustomerSite, Long> {

    List<CustomerSite> findByCustomerIdAndActive(Long customerId, boolean active);

    List<CustomerSite> findByCustomerId(Long customerId);

    @Query("SELECT s FROM CustomerSite s WHERE s.id IN :ids")
    List<CustomerSite> findAllByIds(List<Long> ids);

}

package com.eden.eden_crm_sec_crm_back.repository.lookup;

import com.eden.eden_crm_sec_crm_back.models.lookup.ServiceDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceDetailsRepository extends JpaRepository<ServiceDetails, Long> {
}

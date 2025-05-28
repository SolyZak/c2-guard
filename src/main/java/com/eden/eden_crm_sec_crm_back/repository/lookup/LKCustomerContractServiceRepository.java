package com.eden.eden_crm_sec_crm_back.repository.lookup;

import com.eden.eden_crm_sec_crm_back.base.repository.BaseRepository;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LKCustomerContractServiceRepository extends BaseRepository<LKCustomerContractService, Long> {

    @Query("SELECT s FROM LKCustomerContractService s " +
            "WHERE s.customerService.customerService.id = :serviceId " +
            "AND s.customerContract.id = :customerContractId")
    List<LKCustomerContractService> getByServiceAndAgreement(
            @Param("serviceId") Long serviceId,
            @Param("customerContractId") Long customerContractId);


}

package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.base.repository.BaseRepository;
import com.eden.eden_crm_sec_crm_back.models.SiteDistribution;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SiteDistributionRepository extends BaseRepository<SiteDistribution, Long> {

    @Query("SELECT DISTINCT s.siteDistribution " +
            "FROM LKCustomerContractOperationService s " +
            "WHERE s.workSiteDistributionLocation.site.id = :siteId")
    List<SiteDistribution> findByCustomerContractSite_Id(@Param("siteId") Long siteId);


}

package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.ComplaintEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<ComplaintEntity, Integer> {

    @Query("SELECT c FROM ComplaintEntity c WHERE c.deleted = false AND customer.id = :customerId")
    Page<ComplaintEntity> findByCustomer(@Param("customerId") Long customerId, Pageable pageable);

    @Query("SELECT c " +
            "FROM ComplaintEntity c " +
            "WHERE c.deleted = false " +
            "AND customer.id = :customerId " +
            "AND (CAST(:from AS DATE) IS NULL OR c.createdDate >= :from)" +
            "AND (CAST(:to AS DATE) IS NULL OR c.createdDate <= :to)" +
            "AND (:operationSiteIds IS NULL OR c.customerSite.id IN :operationSiteIds)")
    Page<ComplaintEntity> getReport(@Param("customerId") Long customerId,
                                    @Param("from") LocalDateTime from,
                                    @Param("to") LocalDateTime to,
                                    @Param("operationSiteIds") List<Long> operationSiteIds,
                                    Pageable pageable);

    @Query("SELECT c " +
            "FROM ComplaintEntity c " +
            "WHERE c.deleted = false " +
            "AND (:customerIds IS NULL OR customer.id IN :customerIds) " +
            "AND (CAST(:from AS DATE) IS NULL OR c.createdDate >= :from) " +
            "AND (CAST(:to AS DATE) IS NULL OR c.createdDate <= :to) " +
            "AND (:operationSiteIds IS NULL OR c.customerSite.id IN :operationSiteIds) " +
            "AND (:orgUnitIds IS NULL OR c.orgUnitId IN :orgUnitIds) " +
            "AND (:managerIds IS NULL OR c.managerId IN :managerIds)" +
            "ORDER BY c.id DESC")
    Page<ComplaintEntity> getOrgUnitReport(@Param("customerIds") List<Long> customerIds,
                                           @Param("from") LocalDateTime from,
                                           @Param("to") LocalDateTime to,
                                           @Param("operationSiteIds") List<Long> operationSiteIds,
                                           @Param("orgUnitIds") List<Long> orgUnitIds,
                                           @Param("managerIds") List<Long> managerIds,
                                           Pageable pageable);
}

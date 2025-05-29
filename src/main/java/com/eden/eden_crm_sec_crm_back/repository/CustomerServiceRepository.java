package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerServiceRepository extends JpaRepository<CustomerService, Long> {
    @Query("SELECT l.customerService FROM LKCustomerContractService l WHERE l.customerContract.id = :contractId")
    List<CustomerService> findCustomerServicesByContractId(@Param("contractId") Long contractId);

    @Query("SELECT DISTINCT cs FROM CustomerService cs " +
            "LEFT JOIN FETCH cs.serviceDetails " +
            "WHERE cs.id IN :serviceIds")
    List<CustomerService> findByIdInWithDetails(@Param("serviceIds") List<Long> serviceIds);

    @Query("SELECT s FROM CustomerService s " +
            "LEFT JOIN FETCH s.serviceDetails " +
            "WHERE s.customer.id = :customerId " +
            "AND (s.isDeleted IS NULL OR s.isDeleted = 0)")
    Page<CustomerService> servicesByCustomer(Long customerId, Pageable pageable);

    @Query("SELECT s FROM CustomerService s " +
            "LEFT JOIN FETCH s.serviceDetails " +
            "WHERE s.customer.id = :customerId " +
            "AND (s.isDeleted IS NULL OR s.isDeleted = 0)")
    List<CustomerService> servicesByCustomer(Long customerId);
}

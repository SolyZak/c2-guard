package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.Premise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PremiseRepository extends JpaRepository<Premise, Long>, JpaSpecificationExecutor<Premise> {

    @Query("""
           select p from Premise p
           where p.customer.id = :customerId
             and (p.code = :code or p.name = :name)
           """)
    Optional<Premise> findDuplicateByCustomerAndCodeOrName(@Param("customerId") Long customerId,
                                                           @Param("code") String code,
                                                           @Param("name") String name);

    @Query("""
           select p from Premise p
           where p.customer.id = :customerId
             and p.code = :code
             and p.id <> :id
           """)
    Optional<Premise> findByCustomerIdAndCodeAndIdNot(@Param("customerId") Long customerId,
                                                      @Param("code") String code,
                                                      @Param("id") Long id);

    @Query("""
           select p from Premise p
           where p.customer.id = :customerId
             and p.name = :name
             and p.id <> :id
           """)
    Optional<Premise> findByCustomerIdAndNameAndIdNot(@Param("customerId") Long customerId,
                                                      @Param("name") String name,
                                                      @Param("id") Long id);

    Optional<Premise> findByIdAndCustomer_Id(Long id, Long customerId);

    @Query("select p from Premise p where p.customer.id = :customerId")
    List<Premise> getCustomerPremises(@Param("customerId") Long customerId);
}
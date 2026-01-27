package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.Premise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PremiseRepository extends JpaRepository<Premise, Long>, JpaSpecificationExecutor<Premise> {

    Optional<Premise> findByCodeOrName(String code, String name);

    Optional<Premise> findByCodeAndIdNot(String code, Long id);

    Optional<Premise> findByNameAndIdNot(String name, Long id);

    Optional<Premise> findByIdAndCustomer_Id(Long id, Long customerId);

    @Query("select p from Premise p where p.customer.id = :customerId")
    List<Premise> getCustomerPremises(@Param("customerId") Long customerId);
}

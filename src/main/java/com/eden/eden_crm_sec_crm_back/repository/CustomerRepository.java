package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findFirstByEmail(String email);

    @Query("SELECT c FROM Customer c WHERE c.email = ?1 AND id != ?2")
    Optional<Customer> findFirstByEmailIgnoreId(String email, Long id);

    Optional<Customer> findFirstByCode(String code);

    // Paginated search with keyword
    @Query("SELECT c FROM Customer c WHERE " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.code) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Customer> paginatedCustomers(String keyword, Pageable pageable);

    // Paginated fetch without any filters
    @Query("SELECT c FROM Customer c")
    Page<Customer> paginatedCustomers(Pageable pageable);

    // Count query with keyword search
    @Query("SELECT COUNT(c) FROM Customer c WHERE " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.code) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    long countPaginatedCustomers(String keyword);

    // Paginated search with keyword
    @Query("SELECT c FROM Customer c WHERE " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.code) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Customer> findAll(String keyword);

    @Query("SELECT DISTINCT c FROM Customer c " +
            "JOIN c.customerContracts cc " +
            "WHERE cc.securityCompanyId = :securityCompanyId")
    List<Customer> findCustomersBySecurityCompanyId(Long securityCompanyId);

}

package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.CustomerUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerUserRepository extends JpaRepository<CustomerUser, Long> {
    boolean existsByEmail(String email);

    boolean existsByCode(String code);

    @Query("""
            SELECT u FROM CustomerUser u
            WHERE u.customer.id = :customerId AND u.id = :id
            """)
    Optional<CustomerUser> findByIdAncCustomerId(@Param("id") Long id, @Param("customerId") Long customerId);

    @Query("""
            SELECT u FROM CustomerUser u
            WHERE u.customer.id = :customerId
              AND (
                    :search IS NULL
                    OR :search = ''
                    OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.code) LIKE LOWER(CONCAT('%', :search, '%'))
                  )
            """)
    Page<CustomerUser> searchByCustomerAndSearch(
            @Param("customerId") Long customerId,
            @Param("search") String search,
            Pageable pageable
    );

}

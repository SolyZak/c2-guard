package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.RoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {

    interface RoleSummaryProjection {
        Integer getId();
        String getName();
        String getDescription();
    }

    @EntityGraph(attributePaths = "permissions")
    @Query("select r from RoleEntity r where r.id = :id and r.deleted = false")
    Optional<RoleEntity> findActiveById(@Param("id") Integer id);

    Optional<RoleEntity> findByCustomerIdAndNameIgnoreCaseAndDeletedFalse(Long customerId, String name);

    boolean existsByCustomerIdAndNameIgnoreCaseAndDeletedFalse(Long customerId, String name);

    @Query("""
        select r.id as id, r.name as name, r.description as description
        from RoleEntity r
        where r.deleted = false
          and r.customerId = :customerId
          and (:q is null or :q = '' or lower(r.name) like lower(concat('%', :q, '%')))
        order by r.name asc
    """)
    Page<RoleSummaryProjection> searchSummaries(
            @Param("customerId") Long customerId,
            @Param("q") String q,
            Pageable pageable
    );
}
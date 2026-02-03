package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.RoleEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {

    interface RoleSummaryProjection {
        Integer getId();
        String getName();
        String getDescription();
    }

    @EntityGraph(attributePaths = "permissions")
    Optional<RoleEntity> findById(Integer id);

    Optional<RoleEntity> findByNameIgnoreCase(String name);

    List<RoleEntity> findByDeletedFalseOrderByIdDesc();

    List<RoleSummaryProjection> findByDeletedFalseOrderByNameAsc();
}
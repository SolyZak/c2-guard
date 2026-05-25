package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.RoleLifecycle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleLifecycleRepository extends JpaRepository<RoleLifecycle, Long> {

    Optional<RoleLifecycle> findByUserIdAndEndDateIsNull(Long userId);

    List<RoleLifecycle> findByUserIdOrderByStartDateDesc(Long userId);
}

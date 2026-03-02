package com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.repository;

import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.entity.TaskCheckDefinitionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskCheckDefinitionJpaRepository extends JpaRepository<TaskCheckDefinitionJpaEntity, Long> {

    @Query("SELECT c FROM TaskCheckDefinitionJpaEntity c WHERE c.taskDefinitionId = :taskDefinitionId AND c.deletedAt IS NULL")
    List<TaskCheckDefinitionJpaEntity> findAllByTaskDefinitionIdAndDeletedAtIsNull(@Param("taskDefinitionId") Long taskDefinitionId);

    @Query(nativeQuery = true, value = """
        SELECT
            l.id        AS locationId,
            l.name      AS locationName,
            td.id       AS taskDefinitionId,
            td.name     AS taskDefinitionName,
            tcd.id      AS checkId,
            tcd.name    AS checkName
        FROM location l
                 INNER JOIN patrol_detail pd
                            ON pd.location_id = l.id
                 INNER JOIN task_definition td
                            ON td.id = pd.task_definition_id
                                AND td.customer_id = :customerId
                                AND td.deleted_at IS NULL
                 INNER JOIN task_check_definition tcd
                            ON tcd.task_definition_id = td.id
                                AND tcd.customer_id = :customerId
                                AND tcd.deleted_at IS NULL
        WHERE l.premise_id  = :premiseId
          AND l.customer_id = :customerId
        ORDER BY l.id, td.id, tcd.id
        """)
    <T> List<T> findAllChecksByPremiseAndCustomer(
            @Param("premiseId") Long premiseId,
            @Param("customerId") Long customerId,
            Class<T> projectionType
    );
}

package com.eden.eden_crm_sec_crm_back.patrols.repositories;

import com.eden.eden_crm_sec_crm_back.models.Patrol;
import com.eden.eden_crm_sec_crm_back.patrols.repositories.projections.PatrolPremiseAggregation;
import com.eden.eden_crm_sec_crm_back.patrols.repositories.projections.PatrolReportDetailsAggregation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface PatrolRepository extends JpaRepository<Patrol, Long> {
    @Query(value = """
                    select distinct p.*
                    from patrol p
                    where p.customer_id = :customerId
                    and (
                      :search is null
                      or lower(p.name) like lower(concat('%', :search, '%'))
                      or exists (
                        select 1
                        from location l
                        join patrol_detail lpd on l.id = lpd.location_id
                        where lpd.patrol_id = p.id
                        and lower(l.name) like lower(concat('%', :search, '%'))
                      )
                      or exists (
                        select 1
                        from task t
                        join patrol_detail tpd on t.id = tpd.task_id
                        where tpd.patrol_id = p.id
                        and lower(t.name) like lower(concat('%', :search, '%'))
                      )
                    )
            """, nativeQuery = true)
    Page<Patrol> patrolPaginate(Pageable pageable,
                                          @Param("search") String search,
                                          @Param("customerId") Long customerId);

    @Query("""
            select p from Patrol p where p.customer.id = :customerId
            """)
    List<Patrol> listAllLoggedInCustomerPatrols(@Param("customerId") Long customerId);

    @Query("""
        SELECT
            ptd.location.premise.id AS premiseId,
            ptd.location.premise.name AS premiseName,
            ptd.location.premise.code AS premiseCode,
            pd.patrol.id AS patrolId,
            pd.patrol.name AS patrolName,
            pd.patrol.frequency AS patrolFrequencyType,
            MIN(tes.startDateTime) AS patrolStartDateTime,
            COUNT(ptd.id) AS assignedCount,
            SUM(CASE WHEN tes.status = 'FINISHED' THEN 1 ELSE 0 END) AS finishedCount
        FROM PatrolTaskDistribution ptd
        JOIN ptd.patrolDetail pd
        JOIN TaskExecutionSlot tes ON tes.taskDistribution = ptd.taskDistribution
        WHERE ptd.taskDistribution.contract.id = :contractId
          AND tes.startDateTime BETWEEN :startDateTime AND :endDateTime
          AND (:premiseIds IS NULL OR ptd.location.premise.id IN :premiseIds)
          AND (:patrolIds IS NULL OR pd.patrol.id IN :patrolIds)
          AND (:locationIds IS NULL OR ptd.location.id IN :locationIds)
        GROUP BY premiseId, premiseName, premiseCode, patrolId, patrolName, patrolFrequencyType
    """)
    List<PatrolPremiseAggregation> aggregatePatrolsByPremiseAndPatrol(
            @Param("contractId") Long contractId,
            @Param("startDateTime") OffsetDateTime startDateTime,
            @Param("endDateTime") OffsetDateTime endDateTime,
            @Param("premiseIds") Set<Long> premiseId,
            @Param("patrolIds") Set<Long> patrolIds,
            @Param("locationIds") Set<Long> locationIds
    );

    // ─── [TASK-MIGRATION] COEXISTENCE ──────────────────────────────────────────
    // Converted from JPQL to native SQL: JPQL cannot navigate task_definition via
    // plain Long FK (pd.task_definition_id has no @ManyToOne relation).
    // COALESCE handles both old-path (task != null) and new-path (task_definition_id != null).
    // commentCheck is NULL for new-path — no equivalent column in task_check_execution.
    // CLEANUP: after Phase E remove old-path JOINs (task, task_check, task_check_patrol_execution)
    // and use tdef / tce columns directly.
    // ─── [TASK-MIGRATION] END COEXISTENCE ──────────────────────────────────────
    @Query(value = """
        SELECT
            loc.id                                                       AS locationId,
            loc.name                                                     AS locationName,
            site.id                                                      AS siteId,
            site.name                                                    AS siteName,
            svc.id                                                       AS serviceId,
            cs.service_name                                              AS serviceName,
            COALESCE(t.id,   pd.task_definition_id)                     AS taskId,
            COALESCE(t.name, tdef.name)                                  AS taskName,
            tes.status                                                   AS status,
            COALESCE(tc.evidence, (tce.evidence_image_path IS NOT NULL)) AS hasEvidence,
            COALESCE(tcpe.image,   tce.evidence_image_path)              AS evidenceImage,
            tcpe.comment_check                                           AS commentCheck,
            COALESCE(tcpe.comment, tce.comment)                         AS comment,
            MIN(tes.start_date_time)                                     AS taskStartDateTime,
            MAX(tes.end_date_time)                                       AS taskEndDateTime
        FROM patrol_task_distribution ptd
        JOIN patrol_detail pd
            ON pd.id = ptd.patrol_detail_id
        JOIN location loc
            ON loc.id = ptd.location_id
        JOIN contract_operation_site_distribution_details st
            ON st.id = ptd.service_time_id
        JOIN contract_operation_site_distribution cosd
            ON cosd.id = st.contract_operation_site_distribution_id
        JOIN customer_site site
            ON site.id = cosd.operation_site_id
        JOIN customer_contract_service svc
            ON svc.id = ptd.service_id
        JOIN customer_service_details csd
            ON csd.id = svc.service_details_id
        JOIN customer_service cs
            ON cs.id = csd.customer_service_id
        LEFT JOIN task_execution_slot tes
            ON tes.task_distribution_id = ptd.task_distribution_id
        LEFT JOIN task t
            ON t.id = pd.task_id
        LEFT JOIN task_check tc
            ON tc.task_id = t.id
        LEFT JOIN task_check_patrol_execution tcpe
            ON tcpe.id = tc.id
        LEFT JOIN task_definition tdef
            ON tdef.id = pd.task_definition_id
        LEFT JOIN task_execution te_new
            ON te_new.id = tes.new_task_execution_id
        LEFT JOIN task_check_execution tce
            ON tce.task_execution_id = te_new.id
        WHERE loc.premise_id = :premiseId
          AND pd.patrol_id   = :patrolId
        GROUP BY
            loc.id, loc.name,
            site.id, site.name,
            svc.id, cs.service_name,
            COALESCE(t.id,   pd.task_definition_id),
            COALESCE(t.name, tdef.name),
            tes.status,
            COALESCE(tc.evidence, (tce.evidence_image_path IS NOT NULL)),
            COALESCE(tcpe.image,   tce.evidence_image_path),
            tcpe.comment_check,
            COALESCE(tcpe.comment, tce.comment)
    """, nativeQuery = true)
    List<PatrolReportDetailsAggregation> findPatrolDetails(
        @Param("premiseId") Long premiseId,
        @Param("patrolId") Long patrolId
    );
}

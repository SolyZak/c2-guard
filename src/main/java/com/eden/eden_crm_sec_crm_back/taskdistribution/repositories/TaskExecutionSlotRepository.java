package com.eden.eden_crm_sec_crm_back.taskdistribution.repositories;

import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.TaskExecutionSlot;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.projections.ImmediateTaskReportProjection;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.projections.TodayTaskSlotProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface TaskExecutionSlotRepository extends JpaRepository<TaskExecutionSlot, Long>, JpaSpecificationExecutor<TaskExecutionSlot> {
    @Query("""
        SELECT
            tes.id AS id,
            NULL AS taskId,
            td.taskDefinitionId AS taskDefinitionId,
            p.id AS patrolId,
            CASE
                WHEN td.distributionType = 'PATROL' THEN pp.id
                WHEN td.distributionType = 'IMMEDIATE' AND il.id IS NOT NULL THEN ip.id
                ELSE NULL
            END AS premiseId,
            CASE
                WHEN td.distributionType = 'PATROL' THEN pl.id
                WHEN td.distributionType = 'IMMEDIATE' AND il.id IS NOT NULL THEN il.id
                ELSE NULL
            END AS locationId,
            NULL AS taskName,
            p.name AS patrolName,
            CASE
                WHEN td.distributionType = 'PATROL' THEN pp.name
                WHEN td.distributionType = 'IMMEDIATE' AND il.id IS NOT NULL THEN ip.name
                ELSE NULL
            END AS premiseName,
            CASE
                WHEN td.distributionType = 'PATROL' THEN pl.name
                WHEN td.distributionType = 'IMMEDIATE' AND il.id IS NOT NULL THEN il.name
                ELSE itd.locationName
            END AS locationName,
            p.frequency AS frequency,
            ptd.frequencyRate AS frequencyRate,
            tes.startDateTime AS startDateTime,
            tes.endDateTime AS endDateTime,
            CASE
                WHEN td.distributionType = 'PATROL' THEN pl.accessType
                WHEN td.distributionType = 'IMMEDIATE' AND il.id IS NOT NULL THEN il.accessType
                ELSE NULL
            END AS accessType,
            CASE
                WHEN td.distributionType = 'PATROL' THEN COALESCE(pl.latitude, pp.latitude)
                WHEN td.distributionType = 'IMMEDIATE' AND il.id IS NOT NULL THEN COALESCE(il.latitude, ip.latitude)
                ELSE itd.latitude
            END AS latitude,
            CASE
                WHEN td.distributionType = 'PATROL' THEN COALESCE(pl.longitude, pp.longitude)
                WHEN td.distributionType = 'IMMEDIATE' AND il.id IS NOT NULL THEN COALESCE(il.longitude, ip.longitude)
                ELSE itd.longitude
            END AS longitude,
            tes.status AS status,
            td.id AS taskDistributionId,
            td.distributionType AS distributionType,
            ptd.id AS patrolDistributionId,
            itd.id AS immediateDistributionId
        FROM
            TaskExecutionSlot tes
            JOIN tes.taskAssignment ta
            JOIN tes.taskDistribution td
            LEFT JOIN td.patrolTaskDistribution ptd
            LEFT JOIN td.immediateTaskDistribution itd
            LEFT JOIN ptd.patrolDetail pd
            LEFT JOIN ptd.location pl
            LEFT JOIN itd.location il
            LEFT JOIN pl.premise pp
            LEFT JOIN il.premise ip
            LEFT JOIN pd.patrol p
        WHERE
            tes.customer.id = :customerId
            AND td.contract.id = :contractId
            AND tes.startDateTime < :tomorrow
            AND tes.endDateTime >= :today
            AND (
                (
                    td.distributionType = 'PATROL'
                    AND ptd.service.id = :serviceId
                    AND ptd.serviceTime.id = :serviceTimeId
                    AND ta.slotNumber = :slotNumber
                )
                OR
                (
                    td.distributionType = 'IMMEDIATE'
                    AND ta.workforceId = :workforceId
                )
            )
        ORDER BY
            tes.startDateTime ASC
    """)
    List<TodayTaskSlotProjection> findTodayTasks(
            @Param("customerId") Long customerId,
            @Param("contractId") Long contractId,
            @Param("today") OffsetDateTime today, // midnight today
            @Param("tomorrow") OffsetDateTime tomorrow, // midnight tomorrow
            @Param("serviceId") Long serviceId,
            @Param("serviceTimeId") Long serviceTimeId,
            @Param("slotNumber") Integer slotNumber,
            @Param("workforceId") Long workforceId
    );

    @Query(value = """
        SELECT
            tes.id AS id,
            tes.startDateTime AS startDateTime,
            tes.endDateTime AS endDateTime,
            ta.workforceId AS workforceId,
            CAST(tes.status AS string) AS status,
            COALESCE(l.name, itd.locationName) AS locationName,
            tdef.name AS taskName
        FROM TaskExecutionSlot tes
            JOIN tes.taskDistribution td
            JOIN td.immediateTaskDistribution itd
            JOIN tes.taskAssignment ta
            LEFT JOIN itd.location l
            LEFT JOIN com.eden.eden_crm_sec_crm_back.task_management.infrastructure.persistence.entity.TaskDefinitionJpaEntity tdef
                ON tdef.id = td.taskDefinitionId
        WHERE td.customer.id = :customerId
            AND tes.startDateTime >= :fromDate
            AND tes.startDateTime < :toDate
        ORDER BY tes.startDateTime DESC
        """,
        countQuery = """
        SELECT COUNT(tes)
        FROM TaskExecutionSlot tes
            JOIN tes.taskDistribution td
            JOIN td.immediateTaskDistribution itd
        WHERE td.customer.id = :customerId
            AND tes.startDateTime >= :fromDate
            AND tes.startDateTime < :toDate
        """)
    Page<ImmediateTaskReportProjection> findImmediateTasksReport(
            @Param("customerId") Long customerId,
            @Param("fromDate") OffsetDateTime fromDate,
            @Param("toDate") OffsetDateTime toDate,
            Pageable pageable
    );
}
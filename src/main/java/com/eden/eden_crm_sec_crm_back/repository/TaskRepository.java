package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.Task;
import com.eden.eden_crm_sec_crm_back.models.projections.TodayTasksProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface TaskRepository extends JpaRepository<Task,Long> {

    @Query("""
                SELECT t FROM Task t where t.customer.id = :customerId
            """)
    Page<Task> listTasks(Pageable pageable, @Param("customerId") Long customerId);

    @Query(value = """
            select t.name from task t INNER JOIN patrol_detail tpd on t.id = tpd.task_id where tpd.id = :detailsId 
            """, nativeQuery = true)
    List<String> getTaskNamesByDetailId(@Param("detailsId") Long detailsId);

    @Query("""
                SELECT t FROM Task t where t.customer.id = :customerId
            """)
    List<Task> listTasks(@Param("customerId") Long customerId);

    @Query("""
                SELECT t FROM Task t where t.customer.id = :customerId and id in :taskIds
            """)
    List<Task> listTasksByIds(@Param("customerId") Long customerId, @Param("taskIds") Set<Long> tasksIds);

    @Query("""
                SELECT DISTINCT pd.task
                FROM PatrolDetail pd
                WHERE pd.patrol.id = :patrolId
                  AND pd.location.id = :locationId
                  AND pd.task.customer.id = :customerId
            """)
    List<Task> listLoggedInTasksByPatrolIdAndLocationId(@Param("customerId") Long customerId, @Param("patrolId") Long patrolId, @Param("locationId") Long locationId);

    @Query("""
            SELECT 
                t.name as taskName,
                p.id as patrolId,
                p.name as patrolName,
                l.id as locationId,
                l.accessType as locationAccessType,
                l.name as locationName,
                pr.id as premiseId,
                pr.name as premiseName,
                d.endDate as endDate,
                d.startDate as startDate,
                d.fromTime as startTime,
                d.toTime as endTime,
                d.patrolFrequencyType as patrolFreqType,
                d.id as patrolDistributionId,
                t.id as taskId,
                d.status as periodStatus
            FROM Task t JOIN t.patrolDetails pd 
            JOIN pd.patrol p  
            JOIN pd.location l 
            JOIN l.premise pr
            JOIN ContractOperationSiteDistributionPatrol d on t.id = d.task.id 
            AND l.id = d.location.id
            AND p.id = d.patrol.id
            where d.customer.id = :customerId AND d.customerContract.id = :contractId 
            AND d.customerService.id = :serviceId AND d.site.id = :siteId
            AND :currentDate BETWEEN d.startDate AND d.endDate
            AND d.uniqueId = :uniqueId
            """)
    List<TodayTasksProjection> getTodayTasksByServiceIdAndContractId(@Param("customerId") Long customerId, @Param("contractId") Long contractId
            , @Param("serviceId") Long serviceId, @Param("siteId") Long siteId, @Param("currentDate") LocalDate currentDate, @Param("uniqueId") String uniqueId);
}

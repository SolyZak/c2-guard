package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface TaskRepository extends JpaRepository<Task,Long> {

    @Query("""
                SELECT t FROM Task t where t.customer.id = :customerId
            """)
    Page<Task> listTasks(Pageable pageable, @Param("customerId") Long customerId);

    @Query(value = """
            select t.name from task t INNER JOIN task_patrol_detail tpd on t.id = tpd.task_id where tpd.patrol_detail_id = :detailsId 
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
                SELECT DISTINCT t
                FROM PatrolDetail pd
                JOIN pd.tasks t
                JOIN pd.locations l
                WHERE pd.patrol.id = :patrolId
                  AND l.id = :locationId
                  AND t.customer.id = :customerId
            """)
    List<Task> listLoggedInTasksByPatrolIdAndLocationId(@Param("customerId") Long customerId, @Param("patrolId") Long patrolId, @Param("locationId") Long locationId);
}

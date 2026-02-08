package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
    SELECT t 
    FROM Task t 
    WHERE t.customer.id = :customerId
      AND t.deleted = false
""")
    List<Task> listTasks(@Param("customerId") Long customerId);
}

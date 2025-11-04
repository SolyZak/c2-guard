package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.Patrol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface PatrolRepository extends JpaRepository<Patrol,Long> {

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

}

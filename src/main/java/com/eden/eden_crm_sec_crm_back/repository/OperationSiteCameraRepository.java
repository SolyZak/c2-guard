package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.OperationSiteCamera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperationSiteCameraRepository extends JpaRepository<OperationSiteCamera, Long> {
    @Query("""
            SELECT osc FROM OperationSiteCamera osc
            JOIN FETCH osc.camera c
            JOIN FETCH c.vendor v
            WHERE osc.operationSite.id = :operationSiteId
            """)
    List<OperationSiteCamera> findByOperationSiteIdWithCameraAndVendor(@Param("operationSiteId") Long operationSiteId);
}


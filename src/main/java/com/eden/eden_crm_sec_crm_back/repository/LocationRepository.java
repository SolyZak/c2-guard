package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.Location;
import com.eden.eden_crm_sec_crm_back.models.projections.LocationProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LocationRepository extends JpaRepository<Location,Long> {
    @Query("""
                SELECT l.id as id,l.premise as premise,l.name as name,l.accessType as accessType,l.longitude as longitude,l.latitude as latitude FROM Location l JOIN Premise p on p.id = l.premise.id where p.name = :search OR l.name = :search OR l.accessType = :search OR :search is null
            """)
    Page<LocationProjection> searchByPremiseNameAndLocationNameAndAccessType(@Param("search") String search,
                                                                             Pageable pageable);
}

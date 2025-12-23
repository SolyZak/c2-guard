package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.Location;
import com.eden.eden_crm_sec_crm_back.models.projections.LocationProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LocationRepository extends JpaRepository<Location,Long> {
    @Query("""
                SELECT l.id as id,l.premise as premise,l.name as name,l.accessType as accessType,l.longitude as longitude,l.latitude as latitude FROM Location l JOIN Premise p on p.id = l.premise.id where l.customer.id = :customerId AND (lower(p.name) like lower(concat('%', :search, '%')) OR lower(l.name) like lower(concat('%', :search, '%')) OR lower(l.accessType) like lower(concat('%', :search, '%')) OR :search is null)
            """)
    Page<LocationProjection> searchByPremiseNameAndLocationNameAndAccessType(@Param("search") String search, @Param("customerId") Long customerId,
                                                                             Pageable pageable);
    @Query(value = """
            select l.name from location l INNER JOIN patrol_detail lpd on l.id = lpd.location_id where lpd.id = :detailsId 
            """, nativeQuery = true)
    List<String> getLocationNamesByDetailId(@Param("detailsId") Long detailsId);

    @Query("""
            SELECT l.id as id,l.premise as premise,l.name as name,l.accessType as accessType,l.longitude as longitude,l.latitude as latitude FROM Location l JOIN Premise p on p.id = l.premise.id where l.customer.id = :customerId
            """)
    List<LocationProjection> listAllLoggedInCustomerLocations(@Param("customerId") Long customerId);

    @Query("""
            SELECT l.id as id,l.name as name,l.accessType as accessType,l.longitude as longitude,l.latitude as latitude FROM Location l where l.customer.id = :customerId and id in :locationIds
            """)
    List<LocationProjection> listAllLoggedInCustomerLocationsByIds(@Param("customerId") Long customerId, @Param("locationIds")Set<Long> locationIds);

    @Query("""
            SELECT l.id as id,l.premise as premise,l.name as name,l.accessType as accessType,l.longitude as longitude,l.latitude as latitude FROM Location l JOIN l.patrolDetails pd where l.customer.id = :customerId AND pd.patrol.id = :patrolId
            """)
    List<LocationProjection> listAllLoggedInCustomerLocationsByPatrolId(@Param("customerId") Long customerId, @Param("patrolId") Long patrolId);

    @Query("""
    SELECT l
    FROM Location l
    WHERE l.id = :id
      AND l.accessType = :accessType
      AND l.deleted = false
""")
    Optional<Location> findByIdAndAccessType(
            @Param("id") Long id,
            @Param("accessType") String accessType
    );

}

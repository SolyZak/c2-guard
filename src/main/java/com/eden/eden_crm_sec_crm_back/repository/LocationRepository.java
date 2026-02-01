package com.eden.eden_crm_sec_crm_back.repository;

import com.eden.eden_crm_sec_crm_back.models.Location;
import com.eden.eden_crm_sec_crm_back.models.projections.LocationProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LocationRepository extends JpaRepository<Location,Long> {
    @Query("""
    SELECT l.id as id,
           l.premise as premise,
           l.name as name,
           l.accessType as accessType,
           l.longitude as longitude,
           l.latitude as latitude,
           l.tolerance as tolerance
    FROM Location l 
    JOIN Premise p on p.id = l.premise.id 
    WHERE l.customer.id = :customerId 
    AND (lower(p.name) like lower(concat('%', :search, '%')) 
         OR lower(l.name) like lower(concat('%', :search, '%')) 
         OR lower(l.accessType) like lower(concat('%', :search, '%')) 
         OR :search is null)
    ORDER BY l.id DESC
""")
    Page<LocationProjection> searchByPremiseNameAndLocationNameAndAccessType(
            @Param("search") String search,
            @Param("customerId") Long customerId,
            Pageable pageable
    );

    @Query(value = """
            select l.name from location l INNER JOIN patrol_detail lpd on l.id = lpd.location_id where lpd.id = :detailsId 
            """, nativeQuery = true)
    List<String> getLocationNamesByDetailId(@Param("detailsId") Long detailsId);

    @Query("""
    SELECT l.id as id, l.premise as premise, l.name as name, l.accessType as accessType,
           l.longitude as longitude, l.latitude as latitude, l.tolerance as tolerance 
    FROM Location l JOIN Premise p on p.id = l.premise.id 
    WHERE l.customer.id = :customerId
""")
    List<LocationProjection> listAllLoggedInCustomerLocations(@Param("customerId") Long customerId);

    @Query("""
    SELECT l.id as id, l.name as name, l.accessType as accessType,
           l.longitude as longitude, l.latitude as latitude, l.tolerance as tolerance 
    FROM Location l 
    WHERE l.customer.id = :customerId and id in :locationIds
""")
    List<LocationProjection> listAllLoggedInCustomerLocationsByIds(
            @Param("customerId") Long customerId,
            @Param("locationIds") Set<Long> locationIds);

    @Query("""
    SELECT l.id as id, l.premise as premise, l.name as name, l.accessType as accessType,
           l.longitude as longitude, l.latitude as latitude, l.tolerance as tolerance 
    FROM Location l JOIN l.patrolDetails pd 
    WHERE l.customer.id = :customerId AND pd.patrol.id = :patrolId
""")
    List<LocationProjection> listAllLoggedInCustomerLocationsByPatrolId(
            @Param("customerId") Long customerId,
            @Param("patrolId") Long patrolId);

    @Query(value = """
    SELECT DISTINCT
        l.id            AS id,
        l.name          AS name,
        l.access_type   AS accessType,
        l.longitude     AS longitude,
        l.latitude      AS latitude,
        l.tolerance     AS tolerance
    FROM patrol_detail pd
    JOIN location l
        ON l.id = pd.location_id
    JOIN customer_site cs
        ON cs.premise_id = l.premise_id
    WHERE pd.patrol_id = :patrolId
      AND cs.id = :customerSiteId
      AND l.customer_id = :customerId
      AND l.deleted = false
""", nativeQuery = true)
    List<LocationProjection> findLocationsByPatrolAndCustomerSite(
            @Param("patrolId") Long patrolId,
            @Param("customerSiteId") Long customerSiteId,
            @Param("customerId") Long customerId
    );

    @Query("SELECT l.accessType FROM Location l WHERE l.id = :id")
    Optional<String> findAccessTypeById(@Param("id") Long id);

    interface LocationNoImageProjection {
        Long getId();
        String getName();
        BigDecimal getLatitude();
        BigDecimal getLongitude();
        BigDecimal getTolerance();
        String getAccessType();
    }

    @Query("""
    SELECT l.id AS id,
        l.name AS name,
        l.accessType AS accessType,
        l.longitude AS longitude,
        l.latitude AS latitude,
        l.tolerance AS tolerance
    FROM Location l
    WHERE l.id = :id
      AND l.accessType = :accessType
      AND l.deleted = false
    """)
    Optional<LocationNoImageProjection> findLocationByIdAndAccessType(
            @Param("id") Long id,
            @Param("accessType") String accessType
    );
}

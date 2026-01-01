package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.request.*;
import com.eden.eden_crm_sec_crm_back.dto.response.*;
import com.eden.eden_crm_sec_crm_back.enums.LocationAccessTypeEnum;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.exception.PremiseNotProvided;
import com.eden.eden_crm_sec_crm_back.exception.UserNotProvided;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.Location;
import com.eden.eden_crm_sec_crm_back.models.Premise;
import com.eden.eden_crm_sec_crm_back.models.projections.LocationProjection;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.repository.ContractOperationSiteDistributionPatrolRepository;
import com.eden.eden_crm_sec_crm_back.repository.CustomerRepository;
import com.eden.eden_crm_sec_crm_back.repository.LocationRepository;
import com.eden.eden_crm_sec_crm_back.repository.PremiseRepository;
import com.eden.eden_crm_sec_crm_back.service.LocationService;
import com.eden.eden_crm_sec_crm_back.utils.LocationUtils;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import com.eden.eden_crm_sec_crm_back.utils.QrCodeUtil;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import com.google.zxing.WriterException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.postgresql.PGConnection;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;
    private final PremiseRepository premiseRepository;
    private final CustomerRepository customerRepository;
    private final ContractOperationSiteDistributionPatrolRepository contractOperationSiteDistributionPatrolRepository; // ADD THIS

    private final Utils utils;

    private final EntityManager em;

    @Override
    @Transactional
    public void addNewLocation(AddLocationRequest request) throws IOException, WriterException {

        Customer customer = customerRepository
                .findById(utils.getLoggedInUser().getCustomerId())
                .orElseThrow(UserNotProvided::new);

        Premise premise = premiseRepository.findById(request.getPremiseId())
                .orElseThrow(PremiseNotProvided::new);

        List<Location> locations = new ArrayList<>();

        for (LocationRequestDto dto : request.getLocations()) {
            Location location = new Location();
            location.setPremise(premise);
            location.setName(dto.getLocationName());

            // IMPORTANT: your current condition is impossible (&&). This is the typical correct validation:
            String accessType = dto.getAccessType();
            if (!accessType.equals(LocationAccessTypeEnum.QR_CODE.getType())
                    && !accessType.equals(LocationAccessTypeEnum.SPECIFIC_POINT.getType())) {
                throw new BusinessException(
                        MessageUtil.getMessage("validation.location.locations.accessType.invalid"),
                        HttpStatus.BAD_REQUEST
                );
            }

            location.setAccessType(accessType);
            location.setLatitude(dto.getLatitude());
            location.setLongitude(dto.getLongitude());
            location.setTolerance(dto.getTolerance());
            location.setCustomer(customer);

            locations.add(location);
        }

        // 1) Persist first so IDs are assigned
        locations = locationRepository.saveAll(locations);
        locationRepository.flush(); // ensures inserts happen now (safe)

        // 2) Generate QR for QR_CODE locations
        for (Location location : locations) {
            if (LocationAccessTypeEnum.QR_CODE.getType().equals(location.getAccessType())) {
                byte[] qr = QrCodeUtil.generateQrCode(String.valueOf(location.getId()), 300, 300);
                location.setQrImage(qr);
            }
        }

        // 3) Update with QR images
        locationRepository.saveAll(locations);
    }

    @Override
    @Transactional
    public UpdateLocationResponse updateLocation(Long id, UpdateLocationRequest request) {

        // Get logged-in customer
        Customer customer = customerRepository
                .findById(utils.getLoggedInUser().getCustomerId())
                .orElseThrow(UserNotProvided::new);

        // Check access type FIRST (no LOB loading)
        String accessType = locationRepository.findAccessTypeById(id)
                .orElseThrow(() -> new BusinessException(
                        MessageUtil.getMessage("validation.location.not.found"),
                        HttpStatus.NOT_FOUND
                ));

        if ("qr-code".equals(accessType)) {
            throw new BusinessException(
                    MessageUtil.getMessage("validation.location.qr-code.update.not-allowed"),
                    HttpStatus.BAD_REQUEST
            );
        }

        // Now safe to load full entity
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        MessageUtil.getMessage("validation.location.not.found"),
                        HttpStatus.NOT_FOUND
                ));

        // Verify ownership
        if (!location.getCustomer().getId().equals(customer.getId())) {
            throw new BusinessException(
                    MessageUtil.getMessage("validation.location.unauthorized"),
                    HttpStatus.FORBIDDEN
            );
        }

        // Track what's being updated
        String updatedLocationName = null;
        BigDecimal updatedLongitude = null;
        BigDecimal updatedLatitude = null;
        BigDecimal updatedTolerance = null;
        boolean hasUpdates = false;

        if (request.getLocationName() != null) {
            location.setName(request.getLocationName());
            updatedLocationName = request.getLocationName();
            hasUpdates = true;
        }

        if (request.getLongitude() != null) {
            location.setLongitude(request.getLongitude());
            updatedLongitude = request.getLongitude();
            hasUpdates = true;
        }

        if (request.getLatitude() != null) {
            location.setLatitude(request.getLatitude());
            updatedLatitude = request.getLatitude();
            hasUpdates = true;
        }

        if (request.getTolerance() != null) {
            location.setTolerance(request.getTolerance());
            updatedTolerance = request.getTolerance();
            hasUpdates = true;
        }

        if (hasUpdates) {
            locationRepository.save(location);
        }

        return UpdateLocationResponse.builder()
                .locationName(updatedLocationName)
                .longitude(updatedLongitude)
                .latitude(updatedLatitude)
                .tolerance(updatedTolerance)
                .build();
    }


    @Override
    public PaginateResponse<PremiseLocationDto> getLocationsPaginated(String search, int page, int size) {
        Customer customer = customerRepository.findById(utils.getLoggedInUser().getCustomerId())
                .orElseThrow(UserNotProvided::new);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<LocationProjection> resultPage = locationRepository
                .searchByPremiseNameAndLocationNameAndAccessType(search, customer.getId(), pageable);

        List<PremiseLocationDto> premiseLocationDtos = new ArrayList<>();
        if (resultPage.getContent() != null) {
            for (LocationProjection location : resultPage.getContent()) {
                PremiseLocationDto dto = new PremiseLocationDto(
                        location.getId(),
                        location.getName(),
                        location.getAccessType(),
                        location.getPremise() != null ? location.getPremise().getName() : "",
                        location.getAccessType().equals(LocationAccessTypeEnum.SPECIFIC_POINT.getType())
                                ? "" : Base64.getEncoder().encodeToString(getQrImage(location.getId())),
                        location.getLatitude(),
                        location.getLongitude(),
                        location.getTolerance()
                );
                premiseLocationDtos.add(dto);
            }
        }
        return new PaginateResponse<>(
                premiseLocationDtos,
                page,
                size,
                resultPage.getTotalElements(),
                (long) resultPage.getTotalPages()
        );
    }

    @Override
    public List<LocationWithPremiseDto> findLoggedInCustomerLocations() {
        Customer customer = customerRepository.findById(utils.getLoggedInUser().getCustomerId()).orElseThrow(UserNotProvided::new);
        List<LocationProjection> locations = locationRepository.listAllLoggedInCustomerLocations(customer.getId());
        List<LocationWithPremiseDto> result = new ArrayList<>();
        for (LocationProjection location : locations) {
            LocationWithPremiseDto dto = new LocationWithPremiseDto(location.getName() + "-" + location.getPremise().getName(), location.getId());
            result.add(dto);
        }
        return result;
    }

    @Override
    public List<LocationResponseDto> findLoggedInCustomerLocationsByPatrolId(Long patrolId) {

        Customer customer = customerRepository
                .findById(utils.getLoggedInUser().getCustomerId())
                .orElseThrow(UserNotProvided::new);

        List<LocationProjection> patrolLocations =
                locationRepository.listAllLoggedInCustomerLocationsByPatrolId(
                        customer.getId(), patrolId);

        /* Using a LinkedHashMap just to keep insertion order and ensure uniqueness */
        Map<Long, LocationProjection> uniqueById = new LinkedHashMap<>();
        for (LocationProjection lp : patrolLocations) {
            uniqueById.putIfAbsent(lp.getId(), lp);
        }

        List<LocationResponseDto> result = new ArrayList<>();
        for (LocationProjection lp : uniqueById.values()) {
            result.add(new LocationResponseDto(
                    lp.getId(),
                    lp.getName(),
                    lp.getLongitude() != null ? lp.getLongitude(): null,
                    lp.getLatitude() != null ? lp.getLatitude(): null,
                    lp.getTolerance() != null ? lp.getLatitude(): null
            ));
        }
        return result;
    }

    public byte[] getQrImage(Long id) {
        Session session = em.unwrap(Session.class);
        return session.doReturningWork(connection -> {
            connection.setAutoCommit(false);
            try (PreparedStatement ps = connection.prepareStatement("SELECT qr_image FROM location WHERE id = ?")) {
                ps.setLong(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    long oid = rs.getLong(1);
                    PGConnection pgConnection = connection.unwrap(PGConnection.class);
                    LargeObjectManager lobj = pgConnection.getLargeObjectAPI();
                    LargeObject obj = lobj.open(oid, LargeObjectManager.READ);
                    byte[] data = obj.read((int) obj.size());
                    obj.close();
                    return data;
                }
                return null;
            } catch (Exception e) {
                return new byte[0];
            }
        });

    }

    @Override
    public ValidateQrResponse validateQr(ValidateQrRequest request) {
        final Long id = Long.valueOf(request.getPayload());
        Optional<LocationRepository.QrLocationProjection> opt = locationRepository
                .findQrLocationByIdAndAccessType(id, LocationAccessTypeEnum.QR_CODE.getType());

        if (!id.equals(request.getLocationId())) {
            String msg = MessageUtil.getMessage("validation.qr.locationid.mismatch");
            return new ValidateQrResponse(false, msg, null, null);
        }

        boolean isValid = contractOperationSiteDistributionPatrolRepository
                .existsByIdAndTaskIdAndLocationId(
                        request.getPatrolDistributionId(),
                        request.getTaskId(),
                        request.getLocationId()
                );

        if (!isValid) {
            String msg = MessageUtil.getMessage("validation.qr.patrol.distribution.invalid");
            return new ValidateQrResponse(false, msg, null, null);
        }


        if (opt.isPresent()) {
            var loc = opt.get();
            String msg = MessageUtil.getMessage("validation.qr.success");
            return new ValidateQrResponse(
                    true,
                    msg,
                    loc.getId(),
                    loc.getName()
            );
        }

        String msg = MessageUtil.getMessage("validation.qr.invalid");
        return new ValidateQrResponse(false, msg, null, "");
    }

    @Override
    public ValidateLocationResponse validateLocation(Long locationId, ValidateLocationRequest request) {
        Optional<LocationRepository.QrLocationProjection> opt = locationRepository
                .findQrLocationByIdAndAccessType(locationId, LocationAccessTypeEnum.SPECIFIC_POINT.getType());

        boolean isSuccess = false;

        if (opt.isPresent()) {
            var loc = opt.get();
            isSuccess = LocationUtils.isWithinTolerance(
                    request.latitude(), request.longitude(), loc.getLatitude(), loc.getLongitude(), loc.getTolerance()
            );
        }

        return ValidateLocationResponse.builder()
                .success(isSuccess)
                .build();
    }
}

package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.request.AddLocationRequest;
import com.eden.eden_crm_sec_crm_back.dto.request.LocationRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.request.ValidateLocationRequest;
import com.eden.eden_crm_sec_crm_back.dto.request.ValidateQrRequest;
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
    private final Utils utils;

    private final EntityManager em;

    @Override
    @Transactional
    public void addNewLocation(AddLocationRequest request) throws IOException, WriterException {
        Customer customer = customerRepository.findById(utils.getLoggedInUser().getCustomerId()).orElseThrow(UserNotProvided::new);
        Optional<Premise> premiseOptional = premiseRepository.findById(request.getPremiseId());
        if (!premiseOptional.isPresent())
            throw new PremiseNotProvided();
        List<Location> locations = new ArrayList<>();
        if (request.getLocations() != null) {
            for(LocationRequestDto locationRequestDto : request.getLocations()) {
                Location location = new Location();
                location.setPremise(premiseOptional.get());
                location.setName(locationRequestDto.getLocationName());
                if (locationRequestDto.getAccessType().equals(LocationAccessTypeEnum.QR_CODE.getType()) && locationRequestDto.getAccessType().equals(LocationAccessTypeEnum.SPECIFIC_POINT.getType())) {
                    throw new BusinessException(MessageUtil.getMessage("validation.location.locations.accessType.invalid"), HttpStatus.BAD_REQUEST);
                }
                location.setAccessType(locationRequestDto.getAccessType());
                // Convert BigDecimal to Double for storage in the entity
                location.setLatitude(locationRequestDto.getLatitude());
                location.setLongitude(locationRequestDto.getLongitude());
                location.setCustomer(customer);
                location.setTolerance(locationRequestDto.getTolerance());
                if (location.getAccessType().equals(LocationAccessTypeEnum.QR_CODE.getType())) {
                    byte[] qr = QrCodeUtil.generateQrCode(location.getId().toString(), 300, 300);
                    location.setQrImage(qr);
                }
                locations.add(location);
            }
            locationRepository.saveAll(locations);
        }
    }

    @Override
    public PaginateResponse<PremiseLocationDto> getLocationsPaginated(String search, int page, int size) {
        Customer customer = customerRepository.findById(utils.getLoggedInUser().getCustomerId()).orElseThrow(UserNotProvided::new);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<LocationProjection> resultPage = locationRepository.searchByPremiseNameAndLocationNameAndAccessType(search, customer.getId(), pageable);
        List<PremiseLocationDto> premiseLocationDtos = new ArrayList<>();
        if (resultPage.getContent() != null) {
            for (LocationProjection location : resultPage.getContent()) {
                PremiseLocationDto dto = new PremiseLocationDto(location.getId(), location.getName(), location.getAccessType(),
                        location.getPremise() != null ? location.getPremise().getName() : "",
                        location.getAccessType().equals(LocationAccessTypeEnum.SPECIFIC_POINT.getType())  ? "" : Base64.getEncoder().encodeToString(getQrImage(location.getId())));
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
                    BigDecimal.valueOf(lp.getLongitude()),
                    BigDecimal.valueOf(lp.getLatitude())
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

        final Long id = Long.valueOf(request.getPayload());   // extract text

        Customer customer = customerRepository
                .findById(utils.getLoggedInUser().getCustomerId())
                .orElseThrow(UserNotProvided::new);

        Optional<Location> opt = locationRepository
                .findByIdAndAccessTypeAndCustomerId(
                        id,
                        LocationAccessTypeEnum.QR_CODE.getType(),
                        customer.getId());

        if (opt.isPresent()) {
            Location loc  = opt.get();
            String msg    = MessageUtil.getMessage("validation.qr.success");
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
        Customer customer = customerRepository
                .findById(utils.getLoggedInUser().getCustomerId())
                .orElseThrow(UserNotProvided::new);

        Optional<Location> opt = locationRepository
                .findByIdAndAccessTypeAndCustomerId(
                        locationId,
                        LocationAccessTypeEnum.SPECIFIC_POINT.getType(),
                        customer.getId()
                );

        boolean isSuccess = false;

        if (opt.isPresent()) {
            Location loc = opt.get();
            isSuccess = LocationUtils.isWithinTolerance(
                    request.latitude(), request.longitude(), loc.getLatitude(), loc.getLongitude(), loc.getTolerance()
            );
        }

        return ValidateLocationResponse.builder()
                .success(isSuccess)
                .build();
    }
}

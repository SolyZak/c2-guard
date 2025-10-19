package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.request.AddLocationRequest;
import com.eden.eden_crm_sec_crm_back.dto.request.LocationDto;
import com.eden.eden_crm_sec_crm_back.dto.response.PremiseLocationDto;
import com.eden.eden_crm_sec_crm_back.enums.LocationAccessTypeEnum;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.exception.PremiseNotProvided;
import com.eden.eden_crm_sec_crm_back.models.Location;
import com.eden.eden_crm_sec_crm_back.models.Premise;
import com.eden.eden_crm_sec_crm_back.models.projections.LocationProjection;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.repository.LocationRepository;
import com.eden.eden_crm_sec_crm_back.repository.PremiseRepository;
import com.eden.eden_crm_sec_crm_back.service.LocationService;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import com.eden.eden_crm_sec_crm_back.utils.QrCodeUtil;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;
    private final PremiseRepository premiseRepository;

    private final EntityManager em;

    @Override
    @Transactional
    public void addNewLocation(AddLocationRequest request) throws IOException, WriterException {
        Optional<Premise> premiseOptional = premiseRepository.findById(request.getPremiseId());
        if (!premiseOptional.isPresent())
            throw new PremiseNotProvided();
        List<Location> locations = new ArrayList<>();
        if (request.getLocations() != null) {
            for(LocationDto locationDto : request.getLocations()) {
                Location location = new Location();
                location.setPremise(premiseOptional.get());
                location.setName(locationDto.getLocationName());
                if (locationDto.getAccessType().equals(LocationAccessTypeEnum.QR_CODE.getType()) && locationDto.getAccessType().equals(LocationAccessTypeEnum.SPECIFIC_POINT.getType())) {
                    throw new BusinessException(MessageUtil.getMessage("validation.location.locations.accessType.invalid"), HttpStatus.BAD_REQUEST);
                }
                location.setAccessType(locationDto.getAccessType());
                location.setLatitude(locationDto.getLatitude());
                location.setLongitude(locationDto.getLongitude());
                if (location.getAccessType().equals(LocationAccessTypeEnum.QR_CODE.getType())) {
                    byte[] qr = QrCodeUtil.generateQrCode(location.getName(), 300, 300);
                    location.setQrImage(qr);
                }
                locations.add(location);
            }
            locationRepository.saveAll(locations);
        }
    }

    @Override
    public PaginateResponse<PremiseLocationDto> getLocationsPaginated(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<LocationProjection> resultPage = locationRepository.searchByPremiseNameAndLocationNameAndAccessType(search, pageable);
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
            }
        });
    }
}

package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.request.AddLocationRequest;
import com.eden.eden_crm_sec_crm_back.dto.request.LocationDto;
import com.eden.eden_crm_sec_crm_back.enums.LocationAccessTypeEnum;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.exception.PremiseNotProvided;
import com.eden.eden_crm_sec_crm_back.models.Location;
import com.eden.eden_crm_sec_crm_back.models.Premise;
import com.eden.eden_crm_sec_crm_back.repository.LocationRepository;
import com.eden.eden_crm_sec_crm_back.repository.PremiseRepository;
import com.eden.eden_crm_sec_crm_back.service.LocationService;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import com.eden.eden_crm_sec_crm_back.utils.QrCodeUtil;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;
    private final PremiseRepository premiseRepository;

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
}

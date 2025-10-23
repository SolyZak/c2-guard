package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.request.AddLocationRequest;
import com.eden.eden_crm_sec_crm_back.dto.response.LocationWithPremiseDto;
import com.eden.eden_crm_sec_crm_back.dto.response.PremiseLocationDto;
import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.service.LocationService;
import com.google.zxing.WriterException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "/location")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;
    @PostMapping
    ApiResponse addLocation(@RequestBody @Valid AddLocationRequest request) throws IOException, WriterException {
        locationService.addNewLocation(request);
        return ApiResponse.created();
    }

    @GetMapping
    ApiResponse<PaginateResponse<PremiseLocationDto>> listLocations(@RequestParam(defaultValue = "0", name = "page") Integer page,
                                                                                                   @RequestParam(defaultValue = "10", name = "size") Integer size,
                                                                                                   @RequestParam(required = false, name = "search") String search) {
        return ApiResponse.ok(locationService.getLocationsPaginated(search, page, size));
    }

    @GetMapping("/all")
    ApiResponse<List<LocationWithPremiseDto>> listLocationsNoPagination() {
        return ApiResponse.ok(locationService.findLoggedInCustomerLocations());
    }
}

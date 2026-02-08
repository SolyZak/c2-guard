package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.request.AddLocationRequest;
import com.eden.eden_crm_sec_crm_back.dto.request.UpdateLocationRequest;
import com.eden.eden_crm_sec_crm_back.dto.request.ValidateLocationRequest;
import com.eden.eden_crm_sec_crm_back.dto.request.ValidateQrRequest;
import com.eden.eden_crm_sec_crm_back.dto.response.*;
import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.service.LocationService;
import com.google.zxing.WriterException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PutMapping("/{id}")
    public ApiResponse<UpdateLocationResponse> updateLocation(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateLocationRequest request) {
        return ApiResponse.ok(locationService.updateLocation(id, request));
    }

    @GetMapping
    ApiResponse<PaginateResponse<PremiseLocationDto>> listLocations(
            @RequestParam(defaultValue = "0", name = "page") Integer page,
            @RequestParam(defaultValue = "10", name = "size") Integer size,
            @RequestParam(required = false, name = "search") String search,
            @RequestParam(defaultValue = "true", name = "paginated") boolean paginated
    ) {
        return ApiResponse.ok(locationService.getLocations(search, page, size, paginated));
    }

    @GetMapping("/all")
    ApiResponse<List<LocationWithPremiseDto>> listLocationsNoPagination() {
        return ApiResponse.ok(locationService.findLoggedInCustomerLocations());
    }

    @GetMapping("/all/{patrolId}")
    ApiResponse<List<LocationResponseDto>> listLocationsNoPaginationByPatrolId(@PathVariable("patrolId") Long patrolId) {
        return ApiResponse.ok(locationService.findLoggedInCustomerLocationsByPatrolId(patrolId));
    }

    @GetMapping("/customer-site/{customerSiteId}/patrol/{patrolId}")
    ApiResponse<List<PatrolLocationResponseDto>> getLocationsByCustomerSiteAndPatrol(
            @PathVariable Long customerSiteId,
            @PathVariable Long patrolId
    ) {
        return ApiResponse.ok(locationService.findLocationsByCustomerSiteAndPatrol(customerSiteId,patrolId)
        );
    }

    @PostMapping("/validate-qr")
    public ResponseEntity<ValidateQrResponse> validateQr(@Valid @RequestBody ValidateQrRequest req) {
        ValidateQrResponse resp = locationService.validateQr(req);
        return resp.isSuccess()
                ? ResponseEntity.ok(resp)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
    }

    @PostMapping("/{id}/validate-location")
    public ApiResponse<ValidateLocationResponse> validateLocation(
            @PathVariable("id") Long id,
            @Valid @RequestBody ValidateLocationRequest req
    ) {
        return ApiResponse.ok(locationService.validateLocation(id, req));
    }
}

package com.eden.eden_crm_sec_crm_back.controller;

import com.eden.eden_crm_sec_crm_back.dto.request.AddComplaintRequest;
import com.eden.eden_crm_sec_crm_back.dto.response.Complaint;
import com.eden.eden_crm_sec_crm_back.payload.ApiResponse;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/customer/complaints")
@RequiredArgsConstructor
public class ComplaintsController {
    // HINT: this controller must be used by customers to manage them complaints
    private final ComplaintService complaintService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> addComplaint(@RequestBody @ModelAttribute AddComplaintRequest request) {
        return ApiResponse.ok(complaintService.addComplaint(request));
    }

    @GetMapping
    ApiResponse<PaginateResponse<Complaint>> paginatedComplaintsForCustomer(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return ApiResponse.ok(complaintService.getComplaintsForCustomer(page, size));
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> deleteComplaintForCustomer(@PathVariable Long id) {
        return ApiResponse.ok(complaintService.deleteComplaintForCustomer(id));
    }
}

package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.request.AddComplaintRequest;
import com.eden.eden_crm_sec_crm_back.dto.response.Complaint;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;

import java.util.List;

public interface ComplaintService {
    PaginateResponse<Complaint> getComplaintsForCustomer(Integer page, Integer size);

    String addComplaint(AddComplaintRequest request);

    String deleteComplaintForCustomer(Long id);

    PaginateResponse<Complaint> getComplaintsForSecurityCompany(
            List<Long> customerId,
            List<Long> contractId,
            List<Long> operationSiteId,
            Integer page,
            Integer size
    );
}

package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.request.AddComplaintRequest;
import com.eden.eden_crm_sec_crm_back.dto.response.Complaint;
import com.eden.eden_crm_sec_crm_back.dto.response.ComplaintDetails;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;

import java.time.LocalDate;
import java.util.List;

public interface ComplaintService {
    PaginateResponse<Complaint> getComplaints(Long customerId, Integer page, Integer size);

    ComplaintDetails getComplaintDetails(Integer id);

    String addComplaint(AddComplaintRequest request);

    PaginateResponse<Complaint> getComplaintsReport(
            Long customerId,
            LocalDate fromDate,
            LocalDate toDate,
            List<Long> operationSiteIds,
            Integer page,
            Integer size
    );

    PaginateResponse<Complaint> getOrgUnitReport(
            LocalDate fromDate,
            LocalDate toDate,
            List<Long> customerIds,
            List<Long> operationSiteIds,
            List<Long> orgUnitIds,
            List<Long> managerIds,
            Integer page,
            Integer size
    );
}

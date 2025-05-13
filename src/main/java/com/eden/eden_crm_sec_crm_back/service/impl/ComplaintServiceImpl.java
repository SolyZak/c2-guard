package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.request.AddComplaintRequest;
import com.eden.eden_crm_sec_crm_back.dto.response.Complaint;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.mapper.ComplaintMapper;
import com.eden.eden_crm_sec_crm_back.models.ComplaintEntity;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.CustomerSite;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.repository.ComplaintRepository;
import com.eden.eden_crm_sec_crm_back.service.ComplaintService;
import com.eden.eden_crm_sec_crm_back.service.CustomerService;
import com.eden.eden_crm_sec_crm_back.service.CustomerSiteService;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import com.eden.eden_crm_sec_crm_back.utils.OracleStorageUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {
    private final CustomerService customerService;
    private final ComplaintRepository complaintRepository;
    private final CustomerSiteService customerSiteService;
    private final ComplaintMapper complaintMapper;
    private final OracleStorageUtil oracleStorageUtil;

    @Override
    public PaginateResponse<Complaint> getComplaintsForCustomer(Integer page, Integer size) {
        Customer customer = customerService.getLoggedInCustomer();
        Pageable pageable = PageRequest.of(page, size);
        Page<ComplaintEntity> complaintsPage = complaintRepository.findByCustomer(customer.getId(), pageable);

        List<Complaint> complaintList = complaintsPage.toList().stream()
                .map(
                        c -> complaintMapper.toComplaint(
                                c,
                                c.getEvidencesPaths().stream().map(e -> oracleStorageUtil.getStorageUrl() + e).toList()
                        )
                )
                .toList();

        return new PaginateResponse<>(complaintList,
                complaintsPage.getNumber(),
                complaintsPage.getSize(),
                complaintsPage.getTotalElements(),
                (long) complaintsPage.getTotalPages());
    }

    @Transactional
    @Override
    public String addComplaint(AddComplaintRequest request) {
        Customer customer = customerService.getLoggedInCustomer();
        CustomerSite site = customerSiteService.findOne(request.getOperationSiteId(), customer.getId());

        ComplaintEntity complaint = new ComplaintEntity();
        complaint.setCustomer(customer);
        complaint.setCustomerSite(site);
        complaint.setDescription(request.getDescription());
        complaint.setEvidencesPaths(uploadEvidences(request.getImages()));
        complaintRepository.save(complaint);
        return MessageUtil.getMessage("complaint.created");
    }

    @Override
    public String deleteComplaintForCustomer(Long id) {
        Customer customer = customerService.getLoggedInCustomer();
        ComplaintEntity complaint = complaintRepository.findByIdAndCustomerId(id, customer.getId()).orElseThrow(
                () -> new BusinessException(MessageUtil.getMessage("not-found"), HttpStatus.NOT_FOUND)
        );

        complaint.setDeleted(true);
        complaintRepository.save(complaint);
        return MessageUtil.getMessage("complaint.deleted");
    }

    private List<String> uploadEvidences(List<MultipartFile> images) {
        // todo to be integrated with document service
        return List.of();
    }
}

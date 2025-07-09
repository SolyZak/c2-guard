package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.clients.DocumentsFeignClient;
import com.eden.eden_crm_sec_crm_back.clients.dto.UploadImageRequest;
import com.eden.eden_crm_sec_crm_back.dto.request.AddComplaintRequest;
import com.eden.eden_crm_sec_crm_back.dto.response.Complaint;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.exception.UserNotProvided;
import com.eden.eden_crm_sec_crm_back.mapper.ComplaintMapper;
import com.eden.eden_crm_sec_crm_back.models.*;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.repository.ComplaintRepository;
import com.eden.eden_crm_sec_crm_back.repository.CustomerContractRepository;
import com.eden.eden_crm_sec_crm_back.repository.CustomerRepository;
import com.eden.eden_crm_sec_crm_back.repository.SiteDistributionRepository;
import com.eden.eden_crm_sec_crm_back.service.ComplaintService;
import com.eden.eden_crm_sec_crm_back.service.CustomerSiteService;
import com.eden.eden_crm_sec_crm_back.utils.Constants;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import com.eden.eden_crm_sec_crm_back.utils.OracleStorageUtil;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {
    private final CustomerRepository customerRepository;
    private final ComplaintRepository complaintRepository;
    private final CustomerContractRepository customerContractRepository;
    private final CustomerSiteService customerSiteService;
    private final SiteDistributionRepository siteDistributionRepository;
    private final ComplaintMapper complaintMapper;
    private final OracleStorageUtil oracleStorageUtil;
    private final DocumentsFeignClient documentsFeignClient;
    private final Utils utils;

    @Override
    public PaginateResponse<Complaint> getComplaintsForCustomer(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return mapPageComplaints(complaintRepository.findByCustomer(getLoggedInCustomerId(), pageable));
    }

    @Transactional
    @Override
    public String addComplaint(AddComplaintRequest request) {
        Customer customer = customerRepository.findById(getLoggedInCustomerId()).orElseThrow(UserNotProvided::new);
        CustomerContract customerContract = customerContractRepository.findByIdAndCustomerId(request.getContractId(), customer.getId())
                .orElseThrow(() -> new BusinessException(
                        MessageUtil.getMessage("entity.not-found", new Object[]{MessageUtil.getMessage("contract")}), HttpStatus.BAD_REQUEST)
                );
        SiteDistribution site = siteDistributionRepository.findByIdAndContractId(request.getOperationSiteId(), request.getContractId())
                .orElseThrow(() -> new BusinessException(
                        MessageUtil.getMessage("entity.not-found", new Object[]{MessageUtil.getMessage("operation-site")}), HttpStatus.BAD_REQUEST)
                );

        ComplaintEntity complaint = new ComplaintEntity();
        complaint.setCustomer(customer);
        complaint.setCustomerSite(site.getSite());
        complaint.setContract(customerContract);
        complaint.setDescription(request.getDescription());
        complaint.setEvidencesPaths(uploadEvidences(request.getImages()));
        complaintRepository.save(complaint);
        return MessageUtil.getMessage("complaint.created");
    }

    @Override
    public String deleteComplaintForCustomer(Long id) {
        ComplaintEntity complaint = complaintRepository.findByIdAndCustomerId(id, getLoggedInCustomerId()).orElseThrow(
                () -> new BusinessException(MessageUtil.getMessage("not-found"), HttpStatus.NOT_FOUND)
        );

        complaint.setDeleted(true);
        complaintRepository.save(complaint);
        return MessageUtil.getMessage("complaint.deleted");
    }

    @Override
    public PaginateResponse<Complaint> getComplaintsForSecurityCompany(
            List<Long> customerId,
            List<Long> contractId,
            List<Long> operationSiteId,
            Integer page,
            Integer size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return mapPageComplaints(complaintRepository.paginate(
                        List.of(Utils.getLoggedInSecurityCompanyId()),
                        customerId != null && !customerId.isEmpty() ? customerId : null,
                        contractId != null && !contractId.isEmpty() ? contractId : null,
                        operationSiteId != null && !operationSiteId.isEmpty() ? operationSiteId : null,
                        pageable
                )
        );
    }

    private List<String> uploadEvidences(List<MultipartFile> images) {
        return images.stream().map(image -> {
            String evidenceUri = "%s%s/%s".formatted(Constants.COMPLAINT_PATH, Math.random(), image.getOriginalFilename());
            try {
                documentsFeignClient.uploadImage(
                        UploadImageRequest.builder().image(image).path(evidenceUri).build()
                );
            } catch (Exception e) {
                log.error("Can't upload document for complaint, cause: {}", e.getMessage());
            }
            return evidenceUri;
        }).toList();
    }

    private Long getLoggedInCustomerId() {
        return utils.getLoggedInUser().getCustomerId();
    }

    private PaginateResponse<Complaint> mapPageComplaints(Page<ComplaintEntity> complaintsPage) {
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
}

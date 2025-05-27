package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.base.repository.BaseRepository;
import com.eden.eden_crm_sec_crm_back.base.service.impl.BaseServiceImpl;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.models.CustomerContract;
import com.eden.eden_crm_sec_crm_back.models.CustomerService;
import com.eden.eden_crm_sec_crm_back.models.SiteDistribution;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractOperationService;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import com.eden.eden_crm_sec_crm_back.models.lookup.ServiceDetails;
import com.eden.eden_crm_sec_crm_back.repository.CustomerContractRepository;
import com.eden.eden_crm_sec_crm_back.repository.SiteDistributionRepository;
import com.eden.eden_crm_sec_crm_back.repository.lookup.LKCustomerContractOperationServiceRepository;
import com.eden.eden_crm_sec_crm_back.service.SiteDistributionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@RequiredArgsConstructor
@Transactional
public class SiteDistributionServiceImpl extends BaseServiceImpl<SiteDistribution, Long> implements SiteDistributionService {

    private final LKCustomerContractOperationServiceRepository lkCustomerContractOperationServiceRepository;
    private final SiteDistributionRepository siteDistributionRepository;
    private final CustomerContractRepository customerContractRepository;

    @Override
    protected BaseRepository<SiteDistribution, Long> getRepository() {
        return siteDistributionRepository;
    }

    @Transactional
    public SiteDistribution insert(SiteDistribution entity) {
        validateAgainstContractAndService(entity);
        entity.setOperationServices(new ArrayList<>());
        SiteDistribution savedDistribution = siteDistributionRepository.saveAndFlush(entity);


        if (entity.getActivities() != null && !entity.getActivities().isEmpty()) {
            savedDistribution.setActivities(new HashSet<>(entity.getActivities()));
            savedDistribution = siteDistributionRepository.saveAndFlush(savedDistribution);
        }
        if (entity.getOperationServices() != null && !entity.getOperationServices().isEmpty()) {
            List<LKCustomerContractOperationService> savedServices = new ArrayList<>();
            for (LKCustomerContractOperationService service : entity.getOperationServices()) {
                service.setSiteDistribution(savedDistribution);

                // Initialize empty collections if needed
                if (service.getDays() == null) {
                    throw new BusinessException("Service days must be specified", HttpStatus.BAD_REQUEST);
                }

                LKCustomerContractOperationService savedService =
                        lkCustomerContractOperationServiceRepository.saveAndFlush(service);
                savedServices.add(savedService);
            }
            savedDistribution.setOperationServices(savedServices);
        }
        updateContractStatusToReady(savedDistribution);
        return siteDistributionRepository.saveAndFlush(savedDistribution);
    }

    private void validateAgainstContractAndService(SiteDistribution entity) {
        LKCustomerContractService contractService = entity.getLkCustomerContractService();
        if (contractService == null || contractService.getCustomerService() == null) {
            throw new IllegalArgumentException("Contract service or customer service is missing.");
        }

        CustomerService customerService = contractService.getCustomerService();
        List<ServiceDetails> serviceDetails = customerService.getServiceDetails();
        Long contractQuantity = contractService.getQuantity() != null ? contractService.getQuantity() : 0L;

        long totalQuantity = entity.getOperationServices().stream()
                .filter(op -> op.getQuantity() != null)
                .mapToLong(LKCustomerContractOperationService::getQuantity)
                .sum();

        if (totalQuantity > contractQuantity) {
            throw new IllegalArgumentException("Total operation quantity (" + totalQuantity +
                    ") exceeds contract quantity (" + contractQuantity + ").");
        }

        // Validate total days across all operations
        long totalOperationDays = entity.getOperationServices().stream()
                .mapToLong(op -> op.getDays() != null ? op.getDays().size() : 0)
                .sum();

        long allowedDays = serviceDetails.stream()
                .mapToLong(sd -> sd.getDays() != null ? sd.getDays() : 0L)
                .sum();

        if (totalOperationDays > allowedDays) {
            throw new IllegalArgumentException("Total operation days (" + totalOperationDays +
                    ") exceed allowed service days (" + allowedDays + ").");
        }
    }


    private void updateContractStatusToReady(SiteDistribution siteDistribution) {
        LKCustomerContractService contractService = siteDistribution.getLkCustomerContractService();
        if (contractService != null) {
            CustomerContract contract = contractService.getCustomerContract();
            if (contract != null && !"READY".equals(contract.getStatus())) {
                contract.setStatus("READY");
                customerContractRepository.save(contract);
            }
        }
    }


}

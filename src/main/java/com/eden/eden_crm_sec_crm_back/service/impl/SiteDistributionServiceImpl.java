package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.base.repository.BaseRepository;
import com.eden.eden_crm_sec_crm_back.base.service.impl.BaseServiceImpl;
import com.eden.eden_crm_sec_crm_back.models.SiteDistribution;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractOperationService;
import com.eden.eden_crm_sec_crm_back.repository.SiteDistributionRepository;
import com.eden.eden_crm_sec_crm_back.repository.lookup.LKCustomerContractOperationServiceRepository;
import com.eden.eden_crm_sec_crm_back.service.SiteDistributionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@RequiredArgsConstructor
@Transactional
public class SiteDistributionServiceImpl extends BaseServiceImpl<SiteDistribution, Long> implements SiteDistributionService {

    private final LKCustomerContractOperationServiceRepository lkCustomerContractOperationServiceRepository;
    private final SiteDistributionRepository siteDistributionRepository;

    @Override
    protected BaseRepository<SiteDistribution, Long> getRepository() {
        return siteDistributionRepository;
    }
    @Transactional
    public SiteDistribution insert(SiteDistribution entity) {
        // 1. First save only the SiteDistribution to generate ID
        entity.setOperationServices(new ArrayList<>()); // Initialize empty collection
        SiteDistribution savedDistribution = siteDistributionRepository.saveAndFlush(entity);

        // 2. Process activities (element collection)
        if (entity.getActivities() != null && !entity.getActivities().isEmpty()) {
            savedDistribution.setActivities(new HashSet<>(entity.getActivities()));
            savedDistribution = siteDistributionRepository.saveAndFlush(savedDistribution);
        }

        // 3. Process operation services
        if (entity.getOperationServices() != null && !entity.getOperationServices().isEmpty()) {
            List<LKCustomerContractOperationService> savedServices = new ArrayList<>();
            for (LKCustomerContractOperationService service : entity.getOperationServices()) {
                // Ensure the relationship is set
                service.setSiteDistribution(savedDistribution);

                // Initialize empty collections if needed
                if (service.getDays() == null) {
                    service.setDays(new HashSet<>());
                }

                LKCustomerContractOperationService savedService =
                        lkCustomerContractOperationServiceRepository.saveAndFlush(service);
                savedServices.add(savedService);
            }
            savedDistribution.setOperationServices(savedServices);
        }

        return siteDistributionRepository.saveAndFlush(savedDistribution);
    }

}

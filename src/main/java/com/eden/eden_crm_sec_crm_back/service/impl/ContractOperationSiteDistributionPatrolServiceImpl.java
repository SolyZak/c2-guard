package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.request.ContractDistributionForPatrol;
import com.eden.eden_crm_sec_crm_back.dto.request.LocationsTasksForPatrol;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.exception.UserNotProvided;
import com.eden.eden_crm_sec_crm_back.models.*;
import com.eden.eden_crm_sec_crm_back.models.projections.LocationProjection;
import com.eden.eden_crm_sec_crm_back.repository.*;
import com.eden.eden_crm_sec_crm_back.service.ContractOperationSiteDistributionPatrolService;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ContractOperationSiteDistributionPatrolServiceImpl implements ContractOperationSiteDistributionPatrolService {
    private final ContractOperationSiteDistributionPatrolRepository repository;
    private final PatrolRepository patrolRepository;
    private final CustomerSiteRepository customerSiteRepository;
    private final LocationRepository locationRepository;
    private final TaskRepository taskRepository;
    private final CustomerRepository customerRepository;
    private final Utils utils;
    @Override
    @Transactional
    public void add(List<ContractDistributionForPatrol> requestList, Long contractId, Long serviceId) {

        if (requestList != null && requestList.size() > 0) {
            for (ContractDistributionForPatrol request : requestList) {
                Customer customer = customerRepository.findById(utils.getLoggedInUser().getCustomerId()).orElseThrow(UserNotProvided::new);
                Optional<Patrol> patrolOptional = patrolRepository.findById(request.getPatrolId());
                if (!patrolOptional.isPresent()) {
                    throw new BusinessException(MessageUtil.getMessage("validation.patrol.invalid"), HttpStatus.NOT_FOUND);
                }
                Optional<CustomerSite> customerSiteOptional = customerSiteRepository.findById(request.getSiteId());
                if (!customerSiteOptional.isPresent()) {
                    throw new BusinessException(MessageUtil.getMessage("validation.customer-site.invalid"), HttpStatus.NOT_FOUND);
                }
                Map<Long, List<Long>> locations = new HashMap<>();
                if (request.getLocations() != null) {
                    Set<Long> taskIds = new HashSet<>();
                    Set<Long> locationIds = new HashSet<>();
                    for (LocationsTasksForPatrol locationTasksForPatrol : request.getLocations()) {
                        locationIds.add(locationTasksForPatrol.getLocationId());
                        taskIds.addAll(locationTasksForPatrol.getTasks());
                        locations.put(locationTasksForPatrol.getLocationId(), locationTasksForPatrol.getTasks());
                    }
                    List<Task> tasks = taskRepository.listTasksByIds(customer.getId(), taskIds);
                    if (tasks.size() != taskIds.size()) {
                        throw new BusinessException(MessageUtil.getMessage("validation.task.invalid"), HttpStatus.NOT_FOUND);
                    }
                    List<LocationProjection> locationProjections = locationRepository.listAllLoggedInCustomerLocationsByIds(customer.getId(), locationIds);
                    if (locationProjections.size() != locationIds.size()) {
                        throw new BusinessException(MessageUtil.getMessage("validation.location.invalid"), HttpStatus.NOT_FOUND);
                    }
                }
                ContractOperationSiteDistributionPatrol contractDistributionForPatrol = new ContractOperationSiteDistributionPatrol();
                contractDistributionForPatrol.setPatrolId(request.getPatrolId());
                contractDistributionForPatrol.setSiteId(request.getSiteId());

                contractDistributionForPatrol.setLocations(locations);

                repository.save(contractDistributionForPatrol);
            }
        }
    }
}

package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.base.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.dto.request.AddPatrolDetailRequest;
import com.eden.eden_crm_sec_crm_back.dto.request.AddPatrolRequest;
import com.eden.eden_crm_sec_crm_back.dto.request.BulkReorderPatrolDetailRequest;
import com.eden.eden_crm_sec_crm_back.dto.request.ReorderPatrolDetailRequest;
import com.eden.eden_crm_sec_crm_back.task_management.infrastructure.external.TaskPresenter;
import com.eden.eden_crm_sec_crm_back.dto.response.PatrolKeyValueDto;
import com.eden.eden_crm_sec_crm_back.dto.response.PatrolResponseDetail;
import com.eden.eden_crm_sec_crm_back.dto.response.PatrolResponseDto;
import com.eden.eden_crm_sec_crm_back.enums.PatrolFrequencyEnum;
import com.eden.eden_crm_sec_crm_back.enums.PatrolFrequencyRateEnum;
import com.eden.eden_crm_sec_crm_back.exception.UserNotProvided;
import com.eden.eden_crm_sec_crm_back.models.*;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;
import com.eden.eden_crm_sec_crm_back.repository.CustomerRepository;
import com.eden.eden_crm_sec_crm_back.repository.LocationRepository;
import com.eden.eden_crm_sec_crm_back.patrols.repositories.PatrolRepository;
import com.eden.eden_crm_sec_crm_back.repository.PatrolDetailRepository;
import com.eden.eden_crm_sec_crm_back.service.PatrolsService;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatrolsServiceImpl implements PatrolsService {
    private final PatrolRepository patrolRepository;
    private final LocationRepository locationRepository;
    private final PatrolDetailRepository patrolDetailRepository;
    private final CustomerRepository customerRepository;
    private final Utils utils;
    private final TaskPresenter taskPresenter;

    private static final Set<String> FREQ_BY_NAME = new HashSet<>();
    private static final Set<String> FREQ_BY_RATE = new HashSet<>();

    static {
        for (PatrolFrequencyEnum s : PatrolFrequencyEnum.values()) {
            FREQ_BY_NAME.add(s.getFreq());
        }
        for (PatrolFrequencyRateEnum s : PatrolFrequencyRateEnum.values()) {
            FREQ_BY_RATE.add(s.getRate());
        }
    }

    @Override
    public void addPatrol(AddPatrolRequest request) {
        Customer customer = customerRepository.findById(utils.getLoggedInUser().getCustomerId()).orElseThrow(UserNotProvided::new);
        Long customerId = customer.getId();
        List<PatrolDetail> patrolDetails = new ArrayList<>();
        Patrol patrol = new Patrol();

        Set<String> locationCheckImagePairs = new LinkedHashSet<>();
        int displayOrder = 1;

        for (AddPatrolDetailRequest detailRequest : request.getDetails()) {
            PatrolDetail patrolDetail = new PatrolDetail();
            List<Location> locations = locationRepository.findAllById(detailRequest.getLocations());
            if (locations.size() != detailRequest.getLocations().size()) {
                throw new BusinessException(MessageUtil.getMessage("validation.patrol.locations.invalid"), HttpStatus.BAD_REQUEST);
            }

            List<Long> taskDefIds = detailRequest.getTaskDefinitionIds();
            taskDefIds.forEach(id -> taskPresenter.getTaskDefinition(id));
            for (Location location : locations) {
                for (Long taskDefId : taskDefIds) {
                    patrolDetail.setLocation(location);
                    patrolDetail.setTaskDefinitionId(taskDefId);
                    patrolDetail.setDisplayOrder(displayOrder++);
                    patrolDetail.setPatrol(patrol);
                    patrolDetails.add(patrolDetail);
                    patrolDetail = new PatrolDetail();
                    locationCheckImagePairs.add(taskDefId + ":" + location.getId());
                }
            }
        }
        patrol.setName(request.getPatrolName());
        patrol.setPatrolDetails(patrolDetails);
        if (FREQ_BY_NAME.contains(request.getFrequency()))
            patrol.setFrequency(
                    request.getFrequency()
            );
        else
            throw new BusinessException(MessageUtil.getMessage("validation.patrol.frequency.required"), HttpStatus.BAD_REQUEST);

        if (!isNumeric(request.getFrequencyRate()))
            if (FREQ_BY_RATE.contains(request.getFrequencyRate()))
                patrol.setFrequencyRate(
                        request.getFrequencyRate()
                );
            else
                throw new BusinessException(MessageUtil.getMessage("validation.patrol.frequency_rate.required"), HttpStatus.BAD_REQUEST);
        else
            patrol.setFrequencyRate(
                    request.getFrequencyRate()
            );
        patrol.setCustomer(customer);
        patrolRepository.save(patrol);

        for (String pair : locationCheckImagePairs) {
            String[] parts = pair.split(":");
            Long taskDefId = Long.parseLong(parts[0]);
            Long locationId = Long.parseLong(parts[1]);
            taskPresenter.initLocationCheckImages(taskDefId, locationId, customerId);
        }
    }

    @Override
    public PaginateResponse<PatrolResponseDto> listPatrol(Integer page, Integer size, String search) {
        Customer customer = customerRepository.findById(getLoggedInCustomerId()).orElseThrow(UserNotProvided::new);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Patrol> resultPage = patrolRepository.patrolPaginate(pageable, search, customer.getId());
        List<PatrolResponseDto> patrolResponseDtos = new ArrayList<>();
        if (resultPage.getContent() != null) {
            for (Patrol p : resultPage.getContent()) {
                PatrolResponseDto dto = new PatrolResponseDto();
                dto.setId(p.getId());
                dto.setFrequency(p.getFrequency());
                dto.setFrequencyRate(p.getFrequencyRate());
                dto.setName(p.getName());
                for (PatrolDetail pd : p.getPatrolDetails()) {
                    PatrolResponseDetail detail = new PatrolResponseDetail();
                    detail.setId(pd.getId());
                    detail.setDisplayOrder(pd.getDisplayOrder());
                    detail.setLocations(locationRepository.getLocationNamesByDetailId(pd.getId()));
                    detail.setTaskDefinitions(patrolDetailRepository.getTaskDefinitionNamesByDetailId(pd.getId()));
                    dto.getDetails().add(detail);
                }
                patrolResponseDtos.add(dto);
            }
        }

        return new PaginateResponse<>(
                patrolResponseDtos,
                page,
                size,
                resultPage.getTotalElements(),
                (long) resultPage.getTotalPages()
        );
    }

    @Override
    public List<PatrolKeyValueDto> listAllPatrols() {
        Customer customer = customerRepository.findById(getLoggedInCustomerId()).orElseThrow(UserNotProvided::new);
        List<Patrol> patrols = patrolRepository.listAllLoggedInCustomerPatrols(customer.getId());
        List<PatrolKeyValueDto> result = new ArrayList<>();
        for (Patrol patrol : patrols) {
            PatrolKeyValueDto dto = new PatrolKeyValueDto(patrol.getId(), patrol.getName());
            result.add(dto);
        }
        return result;
    }

    static boolean isNumeric(String str) {
        if (str == null || str.isBlank()) return false;
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    @Transactional
    public void reorderPatrolDetail(Long patrolId, ReorderPatrolDetailRequest request) {
        PatrolDetail detail = patrolDetailRepository.findById(request.getPatrolDetailId())
                .orElseThrow(() -> new BusinessException(
                        MessageUtil.getMessage("validation.patrol.detail.not_found"), HttpStatus.NOT_FOUND));

        if (!detail.getPatrol().getId().equals(patrolId)) {
            throw new BusinessException(
                    MessageUtil.getMessage("validation.patrol.detail.not_belongs"), HttpStatus.BAD_REQUEST);
        }

        int oldPos = detail.getDisplayOrder();
        int newPos = request.getNewPosition();

        if (oldPos == newPos) return;

        long totalDetails = patrolDetailRepository.countByPatrol_Id(patrolId);
        if (newPos > totalDetails) {
            throw new BusinessException(
                    MessageUtil.getMessage("validation.patrol.detail.position.invalid"), HttpStatus.BAD_REQUEST);
        }

        if (oldPos < newPos) {
            patrolDetailRepository.shiftOrdersUp(patrolId, oldPos, newPos);
        } else {
            patrolDetailRepository.shiftOrdersDown(patrolId, newPos, oldPos);
        }

        detail.setDisplayOrder(newPos);
        patrolDetailRepository.save(detail);
    }

    @Override
    @Transactional
    public void bulkReorderPatrolDetails(Long patrolId, BulkReorderPatrolDetailRequest request) {
        List<Long> orderedIds = request.getOrderedDetailIds();

        long totalDetails = patrolDetailRepository.countByPatrol_Id(patrolId);
        if (orderedIds.size() != totalDetails) {
            throw new BusinessException(
                    MessageUtil.getMessage("validation.patrol.detail.bulk.incomplete"), HttpStatus.BAD_REQUEST);
        }

        List<PatrolDetail> details = patrolDetailRepository.findAllById(orderedIds);

        boolean allBelong = details.stream()
                .allMatch(d -> d.getPatrol().getId().equals(patrolId));
        if (!allBelong || details.size() != orderedIds.size()) {
            throw new BusinessException(
                    MessageUtil.getMessage("validation.patrol.detail.not_belongs"), HttpStatus.BAD_REQUEST);
        }

        Map<Long, PatrolDetail> detailMap = details.stream()
                .collect(Collectors.toMap(PatrolDetail::getId, Function.identity()));

        for (int i = 0; i < orderedIds.size(); i++) {
            detailMap.get(orderedIds.get(i)).setDisplayOrder(i + 1);
        }

        patrolDetailRepository.saveAll(details);
    }

    private Long getLoggedInCustomerId() {
        return utils.getLoggedInUser().getCustomerId();
    }
}
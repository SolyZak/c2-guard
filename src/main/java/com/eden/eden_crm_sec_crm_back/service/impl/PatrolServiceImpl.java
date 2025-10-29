package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.base.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.dto.request.AddPatrolDetailRequest;
import com.eden.eden_crm_sec_crm_back.dto.request.AddPatrolRequest;
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
import com.eden.eden_crm_sec_crm_back.repository.PatrolRepository;
import com.eden.eden_crm_sec_crm_back.repository.TaskRepository;
import com.eden.eden_crm_sec_crm_back.service.PatrolService;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatrolServiceImpl implements PatrolService {
    private final PatrolRepository patrolRepository;
    private final LocationRepository locationRepository;
    private final TaskRepository taskRepository;
    private final CustomerRepository customerRepository;
    private final Utils utils;

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
        List<PatrolDetail> patrolDetails = new ArrayList<>();
        Patrol patrol = new Patrol();
        for (AddPatrolDetailRequest detailRequest : request.getDetails()) {
            PatrolDetail patrolDetail = new PatrolDetail();
            List<Location> locations = locationRepository.findAllById(detailRequest.getLocations());
            if (locations.size() != detailRequest.getLocations().size()) {
                throw new BusinessException(MessageUtil.getMessage("validation.patrol.locations.invalid"), HttpStatus.BAD_REQUEST);
            }

            List<Task> tasks = taskRepository.findAllById(detailRequest.getTasks());
            if (tasks.size() != detailRequest.getTasks().size()) {
                throw new BusinessException(MessageUtil.getMessage("validation.patrol.tasks.invalid"), HttpStatus.BAD_REQUEST);
            }
            patrolDetail.setLocations(locations);
            patrolDetail.setTasks(tasks);
            patrolDetail.setPatrol(patrol);
            patrolDetails.add(patrolDetail);
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

        List<Location> locations = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();
        for (PatrolDetail patrolDetail : patrol.getPatrolDetails()) {
            for (Location location : patrolDetail.getLocations()) {
                location.getPatrolDetails().add(patrolDetail);
                locations.add(location);
            }
            for (Task task : patrolDetail.getTasks()) {
                task.getPatrolDetails().add(patrolDetail);
                tasks.add(task);
            }
        }
        locationRepository.saveAll(locations);
        taskRepository.saveAll(tasks);
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
                    List<Long> detailsIds = p.getPatrolDetails().stream()
                            .map(d -> d.getId()).collect(Collectors.toList());
                    for(Long id : detailsIds) {
                        PatrolResponseDetail detail = new PatrolResponseDetail();
                        detail.setLocations(locationRepository.getLocationNamesByDetailId(id));
                        detail.setTasks(taskRepository.getTaskNamesByDetailId(id));
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
    private Long getLoggedInCustomerId() {
        return utils.getLoggedInUser().getCustomerId();
    }
}

package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.base.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.dto.request.AddPatrolDetailRequest;
import com.eden.eden_crm_sec_crm_back.dto.request.AddPatrolRequest;
// ─── [TASK-MIGRATION] NEW ─────────────────────────────────────────────────────
// ACL interface from task_management module.
// CLEANUP: this import stays permanently after Phase E.
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
import com.eden.eden_crm_sec_crm_back.repository.TaskRepository;
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

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatrolsServiceImpl implements PatrolsService {
    private final PatrolRepository patrolRepository;
    private final LocationRepository locationRepository;
    private final TaskRepository taskRepository;
    private final PatrolDetailRepository patrolDetailRepository;
    private final CustomerRepository customerRepository;
    private final Utils utils;
    // ─── [TASK-MIGRATION] NEW ─────────────────────────────────────────────────────
    // ACL port — validates taskDefinitionIds for new-path patrol creation.
    // CLEANUP: this field stays permanently after Phase E.
    private final TaskPresenter taskPresenter;
    // ─── [TASK-MIGRATION] END NEW ─────────────────────────────────────────────────

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

            // ─── [TASK-MIGRATION] dual-mode ────────────────────────────────────────────
            // NEW path: client sends taskDefinitionIds (task_management module).
            // COEXISTENCE path: client sends tasks (legacy Task entity IDs).
            // CLEANUP: remove COEXISTENCE branch and keep only NEW path after Phase E.
            boolean useNewPath = detailRequest.getTaskDefinitionIds() != null
                && !detailRequest.getTaskDefinitionIds().isEmpty();

            if (useNewPath) {
                // ─── [TASK-MIGRATION] NEW ─────────────────────────────────────────────
                // Validate each taskDefinitionId exists in task_management (throws if not).
                // Set patrolDetail.taskDefinitionId; leave task null.
                List<Long> taskDefIds = detailRequest.getTaskDefinitionIds();
                taskDefIds.forEach(id -> taskPresenter.getTaskDefinition(id)); // validate existence
                for (Location location : locations) {
                    for (Long taskDefId : taskDefIds) {
                        patrolDetail.setLocation(location);
                        patrolDetail.setTaskDefinitionId(taskDefId);
                        patrolDetail.setPatrol(patrol);
                        patrolDetails.add(patrolDetail);
                        patrolDetail = new PatrolDetail();
                    }
                }
                // ─── [TASK-MIGRATION] END NEW ─────────────────────────────────────────
            } else {
                // ─── [TASK-MIGRATION] COEXISTENCE ─────────────────────────────────────
                // CLEANUP: delete this branch after Phase E.
                List<Task> tasks = taskRepository.findAllById(detailRequest.getTasks());
                if (tasks.size() != detailRequest.getTasks().size()) {
                    throw new BusinessException(MessageUtil.getMessage("validation.patrol.tasks.invalid"), HttpStatus.BAD_REQUEST);
                }
                for (Location location : locations) {
                    for (Task task : tasks) {
                        patrolDetail.setLocation(location);
                        patrolDetail.setTask(task);
                        patrolDetail.setPatrol(patrol);
                        patrolDetails.add(patrolDetail);
                        patrolDetail = new PatrolDetail();
                    }
                }
                // ─── [TASK-MIGRATION] END COEXISTENCE ─────────────────────────────────
            }
            // ─── [TASK-MIGRATION] END dual-mode ───────────────────────────────────────
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
                        detail.setTaskDefinitions(patrolDetailRepository.getTaskDefinitionNamesByDetailId(id));
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

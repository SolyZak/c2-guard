package com.eden.eden_crm_sec_crm_back.taskdistribution.services;

import com.eden.eden_crm_sec_crm_back.objects.UserData;
import com.eden.eden_crm_sec_crm_back.taskdistribution.entities.PatrolAssignmentAudit;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.PatrolAssignmentAuditRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Writes a single audit row per US2 save. The {@code tasks_*} maps follow the
 * shape {@code {locationId -> [taskDefinitionId,...]}}; {@code delta} is a free
 * JSON object the caller composes (added/removed lists).
 */
@Service
@RequiredArgsConstructor
public class PatrolAssignmentAuditService {

    private final PatrolAssignmentAuditRepository repository;
    private final ObjectMapper objectMapper;

    public PatrolAssignmentAudit record(
            UUID editSessionId,
            Long serviceId,
            Long patrolId,
            Long serviceTimeId,
            Long siteId,
            UserData actor,
            LocalDate cutoffDate,
            Map<Long, ?> tasksBefore,
            Map<Long, ?> tasksAfter,
            Map<String, ?> delta
    ) {
        PatrolAssignmentAudit row = PatrolAssignmentAudit.builder()
                .editSessionId(editSessionId)
                .serviceId(serviceId)
                .patrolId(patrolId)
                .serviceTimeId(serviceTimeId)
                .siteId(siteId)
                .actorUserId(Long.valueOf(actor.getId()))
                .actorUserName(actor.getName())
                .occurredAt(OffsetDateTime.now())
                .cutoffDate(cutoffDate)
                .tasksBefore(writeJson(tasksBefore))
                .tasksAfter(writeJson(tasksAfter))
                .delta(writeJson(delta))
                .build();
        return repository.save(row);
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialise audit payload", e);
        }
    }
}

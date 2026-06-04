package com.eden.eden_crm_sec_crm_back.patrols.services;

import com.eden.eden_crm_sec_crm_back.objects.UserData;
import com.eden.eden_crm_sec_crm_back.patrols.entities.PatrolVersionAudit;
import com.eden.eden_crm_sec_crm_back.patrols.repositories.PatrolVersionAuditRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PatrolVersionAuditService {

    private final PatrolVersionAuditRepository repository;
    private final ObjectMapper objectMapper;

    /**
     * Records one in-place patrol edit. {@code changes} is the field-level delta
     * (name/frequency changed, tasks/locations added/removed); {@code before} /
     * {@code after} are the full definition snapshots.
     */
    public PatrolVersionAudit record(
            UUID editSessionId,
            Long patrolId,
            Long customerId,
            UserData actor,
            LocalDate cutoffDate,
            Object before,
            Object after,
            Object changes,
            List<?> affectedServices
    ) {
        PatrolVersionAudit row = PatrolVersionAudit.builder()
                .editSessionId(editSessionId)
                .patrolId(patrolId)
                .customerId(customerId)
                .actorUserId(Long.valueOf(actor.getId()))
                .actorUserName(actor.getName())
                .occurredAt(OffsetDateTime.now())
                .cutoffDate(cutoffDate)
                .beforeSnapshot(writeJson(before))
                .afterSnapshot(writeJson(after))
                .changes(writeJson(changes))
                .affectedServices(writeJson(affectedServices))
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

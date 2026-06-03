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

    public PatrolVersionAudit record(
            UUID editSessionId,
            Long previousPatrolId,
            Long newPatrolId,
            Long customerId,
            UserData actor,
            LocalDate cutoffDate,
            Object before,
            Object after,
            List<?> affectedServices
    ) {
        PatrolVersionAudit row = PatrolVersionAudit.builder()
                .editSessionId(editSessionId)
                .previousPatrolId(previousPatrolId)
                .newPatrolId(newPatrolId)
                .customerId(customerId)
                .actorUserId(Long.valueOf(actor.getId()))
                .actorUserName(actor.getName())
                .occurredAt(OffsetDateTime.now())
                .cutoffDate(cutoffDate)
                .beforeSnapshot(writeJson(before))
                .afterSnapshot(writeJson(after))
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

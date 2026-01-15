package com.eden.eden_crm_sec_crm_back.dto;

import com.eden.eden_crm_sec_crm_back.entity.Trigger;
import com.eden.eden_crm_sec_crm_back.enums.CreationType;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
@Builder
public class TriggerResponse {
    @NonNull
    private Long id;
    @NonNull
    private Long dbVersion;
    @NonNull
    private String name;

    private String nameAr;

    private String code;

    @NonNull
    private CreationType creationType;

    public static TriggerResponse of(final Trigger trigger) {
        return TriggerResponse.builder()
                .id(trigger.getId())
                .dbVersion(trigger.getDbVersion())
                .name(trigger.getName())
//                .nameAr(trigger.getNameAr())
//                .code(trigger.getCode())
                .creationType(trigger.getCreationType())
                .build();
    }

    public static List<TriggerResponse> of(final List<Trigger> triggers) {
        if (triggers != null && !triggers.isEmpty()) {
            return triggers.stream()
                    .map(TriggerResponse::of)
                    .toList();
        }
        return List.of();
    }
}

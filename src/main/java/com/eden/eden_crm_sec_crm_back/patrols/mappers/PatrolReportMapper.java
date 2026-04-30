package com.eden.eden_crm_sec_crm_back.patrols.mappers;

import com.eden.eden_crm_sec_crm_back.models.Patrol;
import com.eden.eden_crm_sec_crm_back.models.Premise;
import com.eden.eden_crm_sec_crm_back.patrols.dtos.response.*;
import com.eden.eden_crm_sec_crm_back.patrols.repositories.projections.PatrolPremiseAggregation;
import com.eden.eden_crm_sec_crm_back.patrols.repositories.projections.PatrolReportDetailsAggregation;
import com.eden.eden_crm_sec_crm_back.utils.OracleStorageUtil;
import org.mapstruct.*;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PatrolReportMapper {

    @Mapping(target = "id", source = "patrolId")
    @Mapping(target = "name", source = "patrolName")
    @Mapping(target = "startDateTime", source = "patrolStartDateTime")
    @Mapping(target = "frequencyType", source = "patrolFrequencyType")
    @Mapping(target = "assignedTasksCount", source = "assignedCount")
    @Mapping(target = "finishedTasksCount", source = "finishedCount")
    PatrolSummaryDto toPatrolSummary(PatrolPremiseAggregation agg);

    default PatrolReportResponseDto toPatrolReportResponse(Long premiseId, String premiseName, String premiseCode, List<PatrolSummaryDto> patrols) {
        return PatrolReportResponseDto.builder()
                .id(premiseId)
                .name(premiseName)
                .code(premiseCode)
                .patrols(patrols != null ? patrols : List.of())
                .build();
    }

    default List<PatrolReportResponseDto> toPatrolReportResponses(List<PatrolPremiseAggregation> aggregations) {
        if (aggregations == null || aggregations.isEmpty())
            return List.of();

        return aggregations.stream()
                .collect(Collectors.groupingBy(
                        PatrolPremiseAggregation::getPremiseId,
                        Collectors.toList()
                ))
                .values()
                .stream()
                .map(premiseAggs -> {
                    PatrolPremiseAggregation first = premiseAggs.getFirst();
                    List<PatrolSummaryDto> patrols = premiseAggs.stream()
                            .map(this::toPatrolSummary)
                            .toList();

                    return toPatrolReportResponse(
                            first.getPremiseId(),
                            first.getPremiseName(),
                            first.getPremiseCode(),
                            patrols
                    );
                })
                .toList();
    }

    default OffsetDateTime toOffsetDateTime(Instant instant) {
        if (instant == null) return null;
        return instant.atOffset(ZoneOffset.UTC);
    }

    @Mapping(target = "id", source = "agg.taskId")
    @Mapping(target = "name", source = "agg.taskName")
    @Mapping(target = "startDateTime", source = "agg.taskStartDateTime")
    @Mapping(target = "endDateTime", source = "agg.taskEndDateTime")
    @Mapping(target = "evidenceImage", expression = "java(resolveEvidenceImageUrl(agg.getEvidenceImage(), storageUtil))")
    PatrolTaskDetailsResponse toTaskDetailsResponse(PatrolReportDetailsAggregation agg, @Context OracleStorageUtil storageUtil);

    default List<PatrolTaskDetailsResponse> toTaskDetailsResponses(List<PatrolReportDetailsAggregation> aggs, @Context OracleStorageUtil storageUtil) {
        if (aggs == null) return List.of();
        return aggs.stream()
                .map(agg -> toTaskDetailsResponse(agg, storageUtil))
                .toList();
    }

    @Mapping(target = "id", source = "locationId")
    @Mapping(target = "name", source = "locationName")
    @Mapping(target = "tasks", expression = "java(tasks != null ? tasks : List.of())")
    PatrolLocationDetailsResponse toPatrolLocationDetailsResponse(PatrolReportDetailsAggregation agg, @Context List<PatrolTaskDetailsResponse> tasks);

    @Mapping(target = "premiseName", source = "name")
    @Mapping(target = "patrolName", expression = "java(patrol.getName())")
    @Mapping(target = "locations", expression = "java(aggregations != null ? aggregations : List.of())")
    PatrolReportDetailsResponse toPatrolReportDetailsResponse(Premise premise, @Context Patrol patrol, @Context List<PatrolLocationDetailsResponse> aggregations);

    default PatrolReportDetailsResponse toPatrolReportDetails(
            Premise premise,
            Patrol patrol,
            List<PatrolReportDetailsAggregation> aggregations,
            OracleStorageUtil storageUtil
    ) {
        if (aggregations == null || aggregations.isEmpty())
            return null;

        return aggregations.stream()
                .collect(Collectors.groupingBy(PatrolReportDetailsAggregation::getLocationId))
                .values()
                .stream()
                .map(aggs -> {
                    PatrolReportDetailsAggregation first = aggs.getFirst();
                    List<PatrolTaskDetailsResponse> taskDetailsResponses = toTaskDetailsResponses(aggs, storageUtil);
                    return toPatrolLocationDetailsResponse(first, taskDetailsResponses);
                })
                .collect(Collectors.collectingAndThen(Collectors.toList(), responses ->
                        toPatrolReportDetailsResponse(premise, patrol, responses)
                ));
    }

    /**
     * Concatenates the Oracle Object Storage base URL with the relative
     * evidence-image path stored in the database.
     * Returns null when the path is blank so the response field stays null
     * instead of containing a dangling base URL.
     */
    default String resolveEvidenceImageUrl(String relativeImagePath, OracleStorageUtil storageUtil) {
        if (relativeImagePath == null || relativeImagePath.isBlank()) {
            return null;
        }
        return storageUtil.getStorageUrl() + relativeImagePath;
    }
}
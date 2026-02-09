package com.eden.eden_crm_sec_crm_back.patrols.mappers;

import com.eden.eden_crm_sec_crm_back.models.Location;
import com.eden.eden_crm_sec_crm_back.models.Patrol;
import com.eden.eden_crm_sec_crm_back.models.Premise;
import com.eden.eden_crm_sec_crm_back.models.projections.PatrolPremiseAggregation;
import com.eden.eden_crm_sec_crm_back.models.projections.PatrolReportDetailsAggregation;
import com.eden.eden_crm_sec_crm_back.patrols.dtos.response.*;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PatrolReportMapper {

    @Mapping(target = "id", source = "patrolId")
    @Mapping(target = "name", source = "patrolName")
    @Mapping(target = "startDate", source = "patrolStartDate")
    @Mapping(target = "frequencyType", source = "patrolFrequencyType")
    @Mapping(target = "assignedTasksCount", source = "assignedCount")
    @Mapping(target = "finishedTasksCount", source = "finishedCount")
    PatrolSummaryDto toPatrolSummary(PatrolPremiseAggregation agg);

    @Mapping(target = "id", source = "taskId")
    @Mapping(target = "name", source = "taskName")
    @Mapping(target = "startDate", source = "taskStartDate")
    @Mapping(target = "endDate", source = "taskEndDate")
    PatrolTaskDetailsResponse toTaskDetails(PatrolReportDetailsAggregation agg);

    @Mapping(target = "tasks", expression = "java(tasks != null ? tasks : List.of())")
    PatrolLocationDetailsResponse toLocationDetails(Location location, @Context List<PatrolTaskDetailsResponse> tasks);

    @Mapping(target = "patrols", expression = "java(patrols != null ? patrols : List.of())")
    PatrolReportResponseDto toPatrolReportResponse(Premise premise, @Context List<PatrolSummaryDto> patrols);

    @Mapping(target = "premiseName", source = "name")
    @Mapping(target = "patrolName", expression = "java(patrol.getName())")
    @Mapping(target = "locations", expression = "java(locations != null ? locations : List.of())")
    PatrolReportDetailsResponse toPatrolReportDetails(Premise premise, @Context Patrol patrol, @Context List<PatrolLocationDetailsResponse> locations);
}

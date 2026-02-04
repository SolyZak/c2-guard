package com.eden.eden_crm_sec_crm_back.taskdistribution.mappers;

import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response.DistributableTaskResponse;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response.TodayTaskEntryResponse;
import com.eden.eden_crm_sec_crm_back.taskdistribution.dtos.response.TodayTaskExecutionSlotEntryResponse;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.projections.DistributableTaskProjection;
import com.eden.eden_crm_sec_crm_back.taskdistribution.repositories.projections.TodayTaskSlotProjection;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskDistributionMapper {
    @Mapping(target = "executionSlotId", source = "id")
    TodayTaskExecutionSlotEntryResponse toExecutionSlotResponse(TodayTaskSlotProjection projection);

    List<TodayTaskExecutionSlotEntryResponse> toExecutionSlotResponseList(List<TodayTaskSlotProjection> projections);

    @Mapping(target = "patrolFrequency", source = "frequency")
    @Mapping(target = "patrolFrequencyRate", source = "frequencyRate")
    @Mapping(target = "executionSlots", expression = "java(executionSlots)")
    TodayTaskEntryResponse toTodayTaskEntryResponse(TodayTaskSlotProjection projection, @Context List<TodayTaskExecutionSlotEntryResponse> executionSlots);

    DistributableTaskResponse toDistributableTaskResponse(DistributableTaskProjection distributableTaskProjection);

    List<DistributableTaskResponse> toDistributableTaskResponseList(List<DistributableTaskProjection> distributableTaskProjections);
}

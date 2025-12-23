package com.eden.eden_crm_sec_crm_back.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetTime;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TodayTasks {
    String taskName;
    Long patrolId;
    String patrolName;
    Long locationId;
    String locationName;
    Long premiseId;
    String premiseName;
    LocalDate startDate;
    LocalDate endDate;
    OffsetTime startTime;
    OffsetTime endTime;
    String patrolFreqType;

    Long taskId;
    Long patrolDistributionId;

    String periodStatus;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodayTasks that = (TodayTasks) o;
        return taskName.equals(that.taskName) && patrolName.equals(that.patrolName) && locationName.equals(that.locationName) && premiseName.equals(that.premiseName) && patrolFreqType.equals(that.patrolFreqType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskName, patrolName, locationName, premiseName, patrolFreqType);
    }
}

package com.eden.eden_crm_sec_crm_back.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetTime;
import java.util.Objects;

@Data
@AllArgsConstructor
public class TodayTasks {
    String taskName;
    String patrolName;
    String locationName;
    String premiseName;
    LocalDate endDate;
    OffsetTime startTime;
    OffsetTime endTime;
    String patrolFreqType;

    public TodayTasks(String taskName, String patrolName, String locationName, String premiseName, LocalDate endDate, String patrolFreqType) {
        this.taskName = taskName;
        this.patrolName = patrolName;
        this.locationName = locationName;
        this.premiseName = premiseName;
        this.endDate = endDate;
        this.patrolFreqType = patrolFreqType;
    }

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

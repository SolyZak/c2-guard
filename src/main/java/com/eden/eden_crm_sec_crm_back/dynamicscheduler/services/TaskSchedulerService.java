package com.eden.eden_crm_sec_crm_back.dynamicscheduler.services;

import com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.entities.ScheduledTaskEntity;
import com.eden.eden_crm_sec_crm_back.dynamicscheduler.operators.TaskSchedulerOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TaskSchedulerService {

    private final TaskSchedulerOperator schedulerOperator;

    @Transactional
    public void scheduleTaskIfExecuteToday(ScheduledTaskEntity scheduledTask) {
        OffsetDateTime now = OffsetDateTime.now();
        scheduleTaskIfToday(scheduledTask, now);
    }

    @Transactional
    public void scheduleTasksIfExecuteToday(List<ScheduledTaskEntity> scheduledTasks) {
        OffsetDateTime now = OffsetDateTime.now();
        scheduledTasks.forEach(scheduledTask -> scheduleTaskIfToday(scheduledTask, now));
    }

    private void scheduleTaskIfToday(ScheduledTaskEntity scheduledTask, OffsetDateTime now) {
        if (
            !scheduledTask.getIsExecutionFinished()
            && (
                schedulerOperator.isDateTimeTypeAndWithInToday(scheduledTask, now)
                || schedulerOperator.isCronTypeAndWithInToday(scheduledTask, now)
                || schedulerOperator.isStartDateTimeAndDurationAndWithInToday(scheduledTask, now)
            )
        )
            schedulerOperator.scheduleTask(scheduledTask);
    }
}

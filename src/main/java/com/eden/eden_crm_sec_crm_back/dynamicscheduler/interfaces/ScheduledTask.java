package com.eden.eden_crm_sec_crm_back.dynamicscheduler.interfaces;

import com.fasterxml.jackson.databind.JsonNode;

@FunctionalInterface
public interface ScheduledTask {
    JsonNode performTask(JsonNode arguments) throws Exception;
}

package com.eden.eden_crm_sec_crm_back.dynamicscheduler.uuid;

import com.fasterxml.uuid.Generators;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;

import java.util.EnumSet;

public class UuidV7Generator implements BeforeExecutionGenerator {

    @Override
    public Object generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object owner, Object currentValue, EventType eventType) {
        return Generators.timeBasedEpochGenerator().generate();
    }

    @Override
    public EnumSet<EventType> getEventTypes() {
        return EnumSet.of(EventType.INSERT);
    }
}

package com.eden.eden_crm_sec_crm_back.dynamicscheduler.base.generators.uuid;

import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@IdGeneratorType(UuidV7Generator.class)
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface UUIDv7 {

}

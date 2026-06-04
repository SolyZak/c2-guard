package com.eden.eden_crm_sec_crm_back.locks.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Apply to a controller method (or class) to require that the caller currently
 * holds the customer-edit-lock - OR that no lock is held by anyone. Throws
 * {@code LockedByOtherUserException} (423) otherwise.
 *
 * <p>Implemented by {@link CustomerEditLockAspect}.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresCustomerEditLock {
}

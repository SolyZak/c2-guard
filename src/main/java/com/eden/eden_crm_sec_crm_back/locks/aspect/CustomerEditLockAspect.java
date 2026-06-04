package com.eden.eden_crm_sec_crm_back.locks.aspect;

/**
 * This aspect was originally designed for a per-customer lock. With the switch
 * to per-patrol locking the patrol ID must be passed explicitly, so lock
 * enforcement is done in each service method via
 * {@code lockService.requireHeldOrAbsent(patrolId)}.
 *
 * <p>This class and the {@link RequiresCustomerEditLock} annotation are kept
 * as no-ops for now to avoid breaking any remaining references. They can be
 * safely deleted once all references are confirmed removed.
 */
// Intentionally empty — no @Aspect, no @Component
public class CustomerEditLockAspect {
}

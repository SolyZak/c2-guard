package com.eden.eden_crm_sec_crm_back.utils;

import com.eden.eden_crm_sec_crm_back.enums.WeekDaysEnum;
import com.eden.eden_crm_sec_crm_back.exception.UserNotProvided;
import com.eden.eden_crm_sec_crm_back.models.CustomerUser;
import com.eden.eden_crm_sec_crm_back.objects.UserData;
import com.eden.eden_crm_sec_crm_back.objects.UserType;
import com.eden.eden_crm_sec_crm_back.repository.CustomerUserRepository;
import com.eden.eden_crm_sec_crm_back.utils.security.JwtUtil;
import com.eden.eden_crm_sec_crm_back.utils.security.TokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class Utils {

    private final CustomerUserRepository customerUserRepository;

    public static UserData getLoggedInWorkforce() {
        try {
            String token = TokenUtil.getTokenFromRequest();
            String id = JwtUtil.getClaimValue(token, "user_id");
            String name = JwtUtil.getClaimValue(token, "name");
            String userType = JwtUtil.getClaimValue(token, "user_type");
            if (id != null && userType != null && userType.equalsIgnoreCase(UserType.WORKFORCE.name()))
                return UserData.builder()
                        .id(id)
                        .name(name)
                        .type(UserType.WORKFORCE)
                        .build();
            else
                throw new UserNotProvided();
        } catch (UserNotProvided e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception when trying to get logged in workforce data exception: {}", e.getMessage());
            throw new UserNotProvided();
        }
    }

    public static UserData getLoggedInSecurityCompany() {
        try {
            String token = TokenUtil.getTokenFromRequest();
            String id = JwtUtil.getClaimValue(token, "user_id");
            String name = JwtUtil.getClaimValue(token, "name");
            String userType = JwtUtil.getClaimValue(token, "user_type");
            if (id != null && userType != null && userType.equalsIgnoreCase(UserType.SECURITY_COMPANY.name()))
                return UserData.builder()
                        .id(id)
                        .name(name)
                        .type(UserType.SECURITY_COMPANY)
                        .build();
            else
                throw new UserNotProvided();
        } catch (UserNotProvided e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception when trying to get logged in security company data exception: {}", e.getMessage());
            throw new UserNotProvided();
        }
    }

    public static Long getLoggedInWorkforceId() {
        return Long.valueOf(Objects.requireNonNull(getLoggedInWorkforce()).getId());
    }

    public static Long getLoggedInSecurityCompanyId() {
        return Long.valueOf(Objects.requireNonNull(getLoggedInSecurityCompany()).getId());
    }

    public static WeekDaysEnum getTodayWeekDayEnum() {
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        return WeekDaysEnum.valueOf(dayOfWeek.name());
    }

    public static WeekDaysEnum getWeekdayEnum(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return WeekDaysEnum.valueOf(dayOfWeek.name());
    }

    public static UserData getAuditor() {
        try {
            String token = TokenUtil.getTokenFromRequest();
            String id = JwtUtil.getClaimValue(token, "user_id");
            String name = JwtUtil.getClaimValue(token, "name");
            String userType = JwtUtil.getClaimValue(token, "user_type");

            if (id != null && userType != null) {
                return UserData.builder()
                        .id(id)
                        .name(name)
                        .type(UserType.valueOf(userType.toUpperCase()))
                        .build();
            } else
                throw new UserNotProvided();
        } catch (UserNotProvided e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception when trying to get static logged in user data exception: {}", e.getMessage());
            throw new UserNotProvided();
        }
    }

    public UserData getLoggedInUser() {
        try {
            String token = TokenUtil.getTokenFromRequest();
            String id = JwtUtil.getClaimValue(token, "user_id");
            String name = JwtUtil.getClaimValue(token, "name");
            String userType = JwtUtil.getClaimValue(token, "user_type");
            String customerIdClaim = JwtUtil.getClaimValue(token, "customer_id");

            if (id == null || userType == null) {
                log.error("Token missing user_id or user_type. user_id={}, user_type={}", id, userType);
                throw new UserNotProvided();
            }

            Long parsedId = Long.valueOf(id);
            Long customerId;

            // Priority 1: Use customer_id from token if present (new tokens)
            if (customerIdClaim != null && !customerIdClaim.isBlank()) {
                customerId = Long.valueOf(customerIdClaim);
            }
            // Priority 2: For CUSTOMER type, user_id IS the customer_id
            else if (userType.equalsIgnoreCase(UserType.CUSTOMER.name())) {
                customerId = parsedId;
            }
            // Priority 3: For USER_CUSTOMER, look up from DB (legacy tokens without customer_id)
            else if (userType.equalsIgnoreCase(UserType.USER_CUSTOMER.name())) {
                CustomerUser customerUser = customerUserRepository.findById(parsedId)
                        .orElseThrow(() -> {
                            log.error("USER_CUSTOMER with id={} not found in DB. Token may be stale.", parsedId);
                            return new UserNotProvided();
                        });
                customerId = customerUser.getCustomer().getId();
            }
            // Priority 4: Other types (WORKFORCE, SECURITY_COMPANY, EDEN_USER)
            else {
                customerId = parsedId;
            }

            return UserData.builder()
                    .id(id)
                    .name(name)
                    .type(UserType.valueOf(userType.toUpperCase()))
                    .customerId(customerId)
                    .build();
        } catch (UserNotProvided e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception when trying to get logged in user data exception: {}", e.getMessage());
            throw new UserNotProvided();
        }
    }
}
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
            }
            else
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

            if (id != null && userType != null) {
                Long defaultCustomerId = Long.valueOf(id);
                if (userType.equalsIgnoreCase(UserType.USER_CUSTOMER.name())) {
                    CustomerUser customerUser = customerUserRepository.findById(defaultCustomerId)
                            .orElseThrow(UserNotProvided::new);
                    defaultCustomerId = customerUser.getCustomer().getId();
                }
                return UserData.builder()
                        .id(id)
                        .name(name)
                        .type(UserType.valueOf(userType.toUpperCase()))
                        .customerId(defaultCustomerId)
                        .build();
            }
            else
                throw new UserNotProvided();
        } catch (UserNotProvided e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception when trying to get logged in user data exception: {}", e.getMessage());
            throw new UserNotProvided();
        }
    }
}

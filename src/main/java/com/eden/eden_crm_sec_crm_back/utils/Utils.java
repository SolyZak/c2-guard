package com.eden.eden_crm_sec_crm_back.utils;

import com.eden.eden_crm_sec_crm_back.enums.WeekDaysEnum;
import com.eden.eden_crm_sec_crm_back.exception.UserNotProvided;
import com.eden.eden_crm_sec_crm_back.objects.UserData;
import com.eden.eden_crm_sec_crm_back.objects.UserType;
import com.eden.eden_crm_sec_crm_back.utils.security.JwtUtil;
import com.eden.eden_crm_sec_crm_back.utils.security.TokenUtil;
import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.util.Objects;

@Slf4j
public class Utils {

    public static UserData getLoggedInCustomer() {
        try {
            String token = TokenUtil.getTokenFromRequest();
            String id = JwtUtil.getClaimValue(token, "user_id");
            String name = JwtUtil.getClaimValue(token, "name");
            String userType = JwtUtil.getClaimValue(token, "user_type");
            if (id != null && userType != null && userType.equalsIgnoreCase(UserType.CUSTOMER.name()))
                return UserData.builder()
                        .id(id)
                        .name(name)
                        .type(UserType.CUSTOMER)
                        .build();
            else
                throw new UserNotProvided();
        } catch (UserNotProvided e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception when trying to get logged in customer data exception: {}", e.getMessage());
            throw new UserNotProvided();
        }
    }

    public static Long getLoggedInCustomerId() {
        return Long.valueOf(Objects.requireNonNull(getLoggedInCustomer()).getId());
    }

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

    public static Long getLoggedInWorkforceId() {
        return Long.valueOf(Objects.requireNonNull(getLoggedInWorkforce()).getId());
    }

    public static WeekDaysEnum getTodayWeekDayEnum() {
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        return WeekDaysEnum.valueOf(dayOfWeek.name());
    }

    public static WeekDaysEnum getWeekdayEnum(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return WeekDaysEnum.valueOf(dayOfWeek.name());
    }
}

package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.request.AddCustomerUserDto;
import com.eden.eden_crm_sec_crm_back.dto.request.ResetCustomerUserPassword;
import com.eden.eden_crm_sec_crm_back.dto.response.CustomerUserData;
import com.eden.eden_crm_sec_crm_back.dto.response.CustomerUserInfoResponse;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;

public interface CustomerUserService {
    String create(AddCustomerUserDto dto);
    PaginateResponse<CustomerUserData> paginated(
            String search, int page, int size
    );
    String resetPassword(Long id, ResetCustomerUserPassword dto);
    CustomerUserInfoResponse getLoggedInUserInfo();
}

package com.eden.eden_crm_sec_crm_back.service;

import com.eden.eden_crm_sec_crm_back.dto.request.AddCustomerUserDto;
import com.eden.eden_crm_sec_crm_back.dto.request.ResetCustomerUserPassword;
import com.eden.eden_crm_sec_crm_back.dto.request.UpdateCustomerUserDto;
import com.eden.eden_crm_sec_crm_back.dto.response.CustomerUserData;
import com.eden.eden_crm_sec_crm_back.dto.response.CustomerUserInfoResponse;
import com.eden.eden_crm_sec_crm_back.dto.response.RoleLifecycleData;
import com.eden.eden_crm_sec_crm_back.payload.PaginateResponse;

import java.util.List;

public interface CustomerUserService {
    String create(AddCustomerUserDto dto);
    PaginateResponse<CustomerUserData> paginated(
            String search, int page, int size
    );
    String resetPassword(Long id, ResetCustomerUserPassword dto);

    String update(Long id, UpdateCustomerUserDto dto);

    List<RoleLifecycleData> roleHistory(Long id);


        CustomerUserInfoResponse getLoggedInUserInfo();

}

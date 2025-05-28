package com.eden.eden_crm_sec_crm_back.objects;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserData {
    String name;
    String id;
    UserType type;
}

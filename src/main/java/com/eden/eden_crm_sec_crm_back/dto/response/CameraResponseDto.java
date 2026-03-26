package com.eden.eden_crm_sec_crm_back.dto.response;

import lombok.Data;

@Data
public class CameraResponseDto {
    private Long id;
    private String name;
    private String ip;
    private VendorData vendor;
    private CustomerData customer;

    @Data
    public static class VendorData {
        private Long id;
        private String name;
    }

    @Data
    public static class CustomerData {
        private Long id;
        private String name;
        private String email;
    }
}


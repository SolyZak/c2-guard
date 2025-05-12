package com.eden.eden_crm_sec_crm_back.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OracleStorageUtil {

    @Value("${oracle.storage.bucketname}")
    private String bucketName;

    @Value("${oracle.storage.namespace}")
    private String nameSpace;

    @Value("${oracle.storage.url}")
    private String url;

    public String getStorageUrl() {
        return url + "n/" + nameSpace + "/b/" + bucketName + "/o/";
    }
}

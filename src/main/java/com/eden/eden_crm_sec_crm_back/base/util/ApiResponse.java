package com.eden.eden_crm_sec_crm_back.base.util;

import com.eden.eden_crm_sec_crm_back.base.exception.ApiException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    @Builder.Default
    private Boolean success = true;

    private ApiException error;

    @Builder.Default
    private Integer code = HttpStatus.OK.value();

    private T payload;

    private String serviceTime;

    public static <T> ApiResponse<T> ok(T payload) {
        return status(HttpStatus.OK, payload, null);
    }
    public static <T> ApiResponse<T> ok() {
        return status(HttpStatus.OK, null, null);
    }
    public static <T> ApiResponse<T> created(T payload) {
        return status(HttpStatus.CREATED, payload, null);
    }

    public static <T> ApiResponse<T> accepted(T payload) {
        return status(HttpStatus.ACCEPTED, payload, null);
    }

    public static <T> ApiResponse<T> ok(T payload, String serviceTime) {
        return status(HttpStatus.OK, payload, serviceTime);
    }

    public static <T> ApiResponse<T> created(T payload, String serviceTime) {
        return status(HttpStatus.CREATED, payload, serviceTime);
    }
    public static <T> ApiResponse<T> accepted(T payload, String serviceTime) {
        return status(HttpStatus.ACCEPTED, payload, serviceTime);
    }

    public static <T> ApiResponse<T> noContent() {
        return status(HttpStatus.NO_CONTENT, null, null);
    }
    public static <T> ApiResponse<T> error(T payload) {
        return status(HttpStatus.BAD_REQUEST, payload, null);
    }

    public static <T> ApiResponse<T> unprocessableEntity(T payload) {
        return status(HttpStatus.UNPROCESSABLE_ENTITY, payload, LocalDateTime.now().toString());
    }

    private static <T> ApiResponse<T> status(HttpStatus status, T payload, String serviceTime) {
        return new ApiResponse<>(true, null, status.value(), payload, serviceTime);
    }


}
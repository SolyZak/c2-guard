package com.eden.eden_crm_sec_crm_back.payload;

import com.eden.eden_crm_sec_crm_back.exception.ApiException;
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
    public static <T> ApiResponse<T> created() {
        return status(HttpStatus.CREATED, null, null);
    }

    public static <T> ApiResponse<T> error(T payload) {
        return status(HttpStatus.BAD_REQUEST, payload, null);
    }
    public static <T> ApiResponse<T> error(T payload, HttpStatus status) {
        return status(status, payload, null);
    }

    public static <T> ApiResponse<T> unprocessableEntity(T payload) {
        return status(HttpStatus.UNPROCESSABLE_ENTITY, payload, LocalDateTime.now().toString());
    }

    public static ApiResponse<String> badRequest(String message) {
        return status(HttpStatus.BAD_REQUEST, message, LocalDateTime.now().toString());
    }

    private static <T> ApiResponse<T> status(HttpStatus status, T payload, String serviceTime) {
        return new ApiResponse<>(true, null, status.value(), payload, serviceTime);
    }


}
package com.eden.eden_crm_sec_crm_back.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class PaginateResponse<T> {
    List<T> content;
    Integer pageNumber;
    Integer pageSize;
    Long totalElements;
    Long totalPages;
}

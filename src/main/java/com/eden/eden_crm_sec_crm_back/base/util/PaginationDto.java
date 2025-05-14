package com.eden.eden_crm_sec_crm_back.base.util;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.domain.Page;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class PaginationDto<T> {
    private T content;
    private int totalPages;
    private Long totalElements;

    public static <T> PaginationDto<List<T>> fromPage(Page<T> page) {
        return new PaginationDto<>(page.getContent(), page.getTotalPages(), page.getTotalElements());
    }
}

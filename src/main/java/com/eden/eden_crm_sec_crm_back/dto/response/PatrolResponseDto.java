package com.eden.eden_crm_sec_crm_back.dto.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class PatrolResponseDto {
    Long id;
    String name;
    String frequency;
    String frequencyRate;
    List<PatrolResponseDetail> details = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatrolResponseDto dto = (PatrolResponseDto) o;
        return Objects.equals(id, dto.id);
    }
}

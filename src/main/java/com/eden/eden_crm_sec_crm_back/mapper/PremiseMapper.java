package com.eden.eden_crm_sec_crm_back.mapper;

import com.eden.eden_crm_sec_crm_back.dto.request.PremiseRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.response.PremiseResponseDto;
import com.eden.eden_crm_sec_crm_back.models.Premise;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PremiseMapper {
    Premise toEntity(PremiseRequestDto dto);

    PremiseResponseDto fromEntity(Premise entity);

}

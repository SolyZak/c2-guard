package com.eden.eden_crm_sec_crm_back.mapper;

import com.eden.eden_crm_sec_crm_back.dto.request.PremiseRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.request.PremiseUpdateRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.response.PremiseResponseDto;
import com.eden.eden_crm_sec_crm_back.models.Premise;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface PremiseMapper {

    Premise toEntity(PremiseRequestDto dto);

    PremiseResponseDto fromEntity(Premise entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(PremiseUpdateRequestDto dto, @MappingTarget Premise entity);

}

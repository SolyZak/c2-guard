package com.eden.eden_crm_sec_crm_back.base.mapper;
import com.eden.eden_crm_sec_crm_back.base.dto.BaseDto;
import org.mapstruct.MappingTarget;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

public interface BaseMapper <T, DTO>{

    DTO map (T t);
    /**
     *
     * @param dto
     * @return
     */
    T unMap (DTO dto);

    BaseDto<Serializable> mapToBase (T t);

    T unMap (@MappingTarget T t , DTO dto);
    /**
     *
     * @param t
     * @return
     */
    List<DTO> map (List<T> t);

    Set<DTO> map (Set<T> t);
    /**
     *
     * @param dto
     * @return
     */
    List<T> unMap (List<DTO> dto);
}

package com.eden.eden_crm_sec_crm_back.service.impl;

import com.eden.eden_crm_sec_crm_back.dto.request.PremiseRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.request.PremiseUpdateRequestDto;
import com.eden.eden_crm_sec_crm_back.dto.response.PremiseResponseDto;
import com.eden.eden_crm_sec_crm_back.exception.UserNotProvided;
import com.eden.eden_crm_sec_crm_back.exception.ValidationException;
import com.eden.eden_crm_sec_crm_back.mapper.PremiseMapper;
import com.eden.eden_crm_sec_crm_back.models.Customer;
import com.eden.eden_crm_sec_crm_back.models.Premise;
import com.eden.eden_crm_sec_crm_back.repository.CustomerRepository;
import com.eden.eden_crm_sec_crm_back.repository.PremiseRepository;
import com.eden.eden_crm_sec_crm_back.utils.MessageUtil;
import com.eden.eden_crm_sec_crm_back.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PremiseServiceImpl {
    private final CustomerRepository customerRepository;

    private final PremiseRepository premiseRepository;
    private final PremiseMapper premiseMapper;
    private final Utils utils;

    public PremiseResponseDto addPremise(PremiseRequestDto requestDto) {
        Customer customer = customerRepository.findById(utils.getLoggedInUser().getCustomerId()).orElseThrow(UserNotProvided::new);
        Premise premise = premiseMapper.toEntity(requestDto);
        Optional<Premise> premiseExists = premiseRepository.findByCodeOrName(premise.getCode(), premise.getName());
        if (premiseExists.isPresent()) {
            throw new ValidationException("message", MessageUtil.getMessage("validation.premise.duplicate"));
        }
        premise.setCustomer(customer);

        premiseRepository.save(premise);

        return premiseMapper.fromEntity(premise);
    }

    public List<PremiseResponseDto> getPremises() {
        Customer customer = customerRepository.findById(utils.getLoggedInUser().getCustomerId()).orElseThrow(UserNotProvided::new);
        return premiseRepository.getCustomerPremises(customer.getId()).stream().map(premiseMapper::fromEntity).toList();
    }

    public PremiseResponseDto getPremiseById(Long premiseId) {
        Long customerId = utils.getLoggedInUser().getCustomerId();

        Premise premise = premiseRepository
                .findByIdAndCustomer_Id(premiseId, customerId)
                .orElseThrow(() -> new ValidationException("message", "Premise not found"));

        return premiseMapper.fromEntity(premise);
    }

    public PremiseResponseDto updatePremisePartial(Long premiseId, PremiseUpdateRequestDto requestDto) {
        Long customerId = utils.getLoggedInUser().getCustomerId();

        Premise premise = premiseRepository
                .findByIdAndCustomer_Id(premiseId, customerId)
                .orElseThrow(() -> new ValidationException("message", "Premise not found"));


        if (requestDto.getCode() != null) {
            premiseRepository.findByCodeAndIdNot(requestDto.getCode(), premiseId)
                    .ifPresent(p -> {
                        throw new ValidationException("message", MessageUtil.getMessage("validation.premise.duplicate"));
                    });
        }

        if (requestDto.getName() != null) {
            premiseRepository.findByNameAndIdNot(requestDto.getName(), premiseId)
                    .ifPresent(p -> {
                        throw new ValidationException("message", MessageUtil.getMessage("validation.premise.duplicate"));
                    });
        }

        // Partial update: nulls are ignored
        premiseMapper.updateEntityFromDto(requestDto, premise);

        premiseRepository.save(premise);
        return premiseMapper.fromEntity(premise);
    }
}

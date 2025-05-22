package com.eden.eden_crm_sec_crm_back.service.impl.lookup;

import com.eden.eden_crm_sec_crm_back.base.repository.BaseRepository;
import com.eden.eden_crm_sec_crm_back.base.service.impl.BaseServiceImpl;
import com.eden.eden_crm_sec_crm_back.dto.CustomerServiceDetailsDTO;
import com.eden.eden_crm_sec_crm_back.dto.lookup.LKCustomerContractServiceDto;
import com.eden.eden_crm_sec_crm_back.dto.lookup.ServiceDetailsCustomDto;
import com.eden.eden_crm_sec_crm_back.models.CustomerService;
import com.eden.eden_crm_sec_crm_back.models.lookup.LKCustomerContractService;
import com.eden.eden_crm_sec_crm_back.repository.lookup.LKCustomerContractServiceRepository;
import com.eden.eden_crm_sec_crm_back.service.lookup.LKCustomerContractServiceOfService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LKCustomerContractServiceOfServiceImpl extends BaseServiceImpl<LKCustomerContractService, Long> implements LKCustomerContractServiceOfService {

    private final LKCustomerContractServiceRepository lkCustomerContractServiceRepository;

    @Override
    protected BaseRepository<LKCustomerContractService, Long> getRepository() {
        return lkCustomerContractServiceRepository;
    }
    @Transactional(readOnly = true)
    @Override
    public List<LKCustomerContractServiceDto> getByAgreementWithServices(Long agreementId, Long serviceId) {
        List<LKCustomerContractService> entities =
                lkCustomerContractServiceRepository.getByServiceAndAgreement(serviceId, agreementId);

        return entities.stream()
                .map(entity -> {
                    CustomerService service = entity.getCustomerService();
                    CustomerServiceDetailsDTO serviceDTO = new CustomerServiceDetailsDTO(
                            service.getId(),
                            service.getServiceName(),
                            service.getServiceDetails().stream()
                                    .map(detail -> new ServiceDetailsCustomDto(
                                            detail.getId(),
                                            detail.getHours(),
                                            detail.getDays()
                                    ))
                                    .collect(Collectors.toList())
                    );

                    return new LKCustomerContractServiceDto(
                            entity.getId(),
                            entity.getQuantity(),
                            entity.getUnitPrice(),
                            serviceDTO.getId(),
                            entity.getCustomerContract().getId()
                    );
                })
                .collect(Collectors.toList());
    }
}

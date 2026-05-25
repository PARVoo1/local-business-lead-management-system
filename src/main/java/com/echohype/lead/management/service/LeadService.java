package com.echohype.lead.management.service;

import com.echohype.lead.management.dto.LeadResponseDto;
import com.echohype.lead.management.entity.Lead;
import com.echohype.lead.management.entity.Status;
import com.echohype.lead.management.repository.LeadRepository;
import com.echohype.lead.management.specification.LeadSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadRepository leadRepository;

    public List<LeadResponseDto> search(Status status, Integer month) {
        Specification<Lead> filter = Specification
                .allOf(LeadSpecification.hasStatus(status)
                        ,LeadSpecification.hasMonth(month));
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Lead> all = leadRepository.findAll(filter, sort);

        return all.stream().map(this::toDto).toList();

    }

    public void saveLead(Lead lead) {
        leadRepository.save(lead);
    }


    private LeadResponseDto toDto(Lead lead) {
        LeadResponseDto leadResponseDto = new LeadResponseDto();
        leadResponseDto.setName(lead.getName());
        leadResponseDto.setEmail(lead.getEmail());
        leadResponseDto.setPhoneNumber(lead.getPhoneNumber());
        leadResponseDto.setBusinessName(lead.getBusinessName());
        leadResponseDto.setBiggestChallenge(lead.getBiggestChallenge());
        leadResponseDto.setStatus(lead.getStatus());
        leadResponseDto.setCreatedAt(lead.getCreatedAt());
        return leadResponseDto;
    }



}

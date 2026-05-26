package com.echohype.lead.management.service;

import com.echohype.lead.management.dto.FieldDto;
import com.echohype.lead.management.dto.LeadResponseDto;
import com.echohype.lead.management.dto.WebhookDto;
import com.echohype.lead.management.entity.Lead;
import com.echohype.lead.management.entity.Status;
import com.echohype.lead.management.repository.LeadRepository;
import com.echohype.lead.management.specification.LeadSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
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

    public void saveLead(WebhookDto dto) {
        if (dto == null || dto.getFields() == null) {
            return;
        }

        Lead newLead = new Lead();

        for (FieldDto field : dto.getFields()) {


            if (field.getId() == null || field.getValue() == null || field.getValue().trim().isEmpty()) {
                continue;
            }

            String fieldId = field.getId().toLowerCase();
            String fieldValue = field.getValue().trim();

            switch (fieldId) {
                case "name":
                    newLead.setName(fieldValue);
                    break;
                case "email":
                    newLead.setEmail(fieldValue);
                    break;
                case "phone":
                    newLead.setPhoneNumber(fieldValue);
                    break;
                case "business_name":
                    newLead.setBusinessName(fieldValue);
                    break;
                case "biggest_challenge":
                    newLead.setBiggestChallenge(fieldValue);
                    break;
            }
        }

        if (newLead.getName() == null || newLead.getBiggestChallenge() == null) {
            log.error("Form rejected: Missing mandatory Name or Challenge.");
            return;
        }

        leadRepository.save(newLead);
        log.info("Successfully saved new lead: " + newLead.getName());
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

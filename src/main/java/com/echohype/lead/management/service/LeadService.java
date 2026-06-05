package com.echohype.lead.management.service;

import com.echohype.lead.management.dto.LeadResponseDto;
import com.echohype.lead.management.dto.WebsiteLeadDto;
import com.echohype.lead.management.dto.WhatsAppLeadDto;
import com.echohype.lead.management.entity.Lead;
import com.echohype.lead.management.entity.Status;
import com.echohype.lead.management.entity.User;
import com.echohype.lead.management.exception.LeadNotFoundException;
import com.echohype.lead.management.repository.LeadRepository;
import com.echohype.lead.management.repository.UserRepository;
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
    private final UserRepository userRepository;


    public void updateStatus(Long id,Status status,String loggedInUserName) {

        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new LeadNotFoundException(id));
        if (!lead.getUser().getUsername().equals(loggedInUserName)) {
            log.warn("Security Alert: User {} attempted to modify lead {} owned by {}",
                    loggedInUserName, id, lead.getUser().getUsername());
            throw new SecurityException("You do not have permission to modify this lead.");
        }
        lead.setStatus(status);
        leadRepository.save(lead);

    }

    public List<LeadResponseDto> search(Status status, Integer month, String userName) {
        Specification<Lead> filter = Specification
                .allOf(LeadSpecification.hasStatus(status)
                        ,LeadSpecification.hasMonth(month),
                        LeadSpecification.hasUsername(userName));
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Lead> all = leadRepository.findAll(filter, sort);

        return all.stream().map(this::toDto).toList();

    }

    public void saveLead(WebsiteLeadDto dto, String userName) {
        if (dto == null) {
            return;
        }

        if (dto.getName() == null || dto.getName().trim().isEmpty() ||
                dto.getBiggestChallenge() == null || dto.getBiggestChallenge().trim().isEmpty()) {
            log.error("Form rejected: Missing mandatory Name or Challenge.");
            return;
        }
        User businessOwner = userRepository.findByUsername(userName);
        if (businessOwner == null) {
            log.error("Form rejected: Business owner '{}' not found.", userName);
            return;
        }

        Lead newLead = new Lead();
        newLead.setName(dto.getName());
        newLead.setEmail(dto.getEmail());
        newLead.setPhoneNumber(dto.getPhone());
        newLead.setBusinessName(dto.getBusinessName());
        newLead.setBiggestChallenge(dto.getBiggestChallenge());
        newLead.setUser(businessOwner);

        leadRepository.save(newLead);
        log.info("Successfully saved new lead: " + newLead.getName());
    }
    public void saveWhatsappLead(WhatsAppLeadDto dto, String userName) {
        if(dto == null || dto.getEntry()==null||dto.getEntry().isEmpty()) {
            return;
        }
        var changes = dto.getEntry().getFirst().getChanges();

        if (changes == null || changes.isEmpty()) {
            return;
        }
        var value = changes.getFirst().getValue();
        if (value.getMessages() == null || value.getContacts() == null) {
            return;
        }

        User businessOwner = userRepository.findByUsername(userName);
        if (businessOwner == null) {
            return;
        }
        String clientName = value.getContacts().getFirst().getProfile().getName();
        String clientPhone = value.getContacts().getFirst().getWa_id();
        String message = value.getMessages().getFirst().getText().getBody();

        Lead newLead = new Lead();

        newLead.setName(clientName);
        newLead.setPhoneNumber("WA"+clientPhone);
        newLead.setBiggestChallenge(message);
        newLead.setBusinessName("WhatsApp DM");
        newLead.setEmail("N/A");
        newLead.setStatus(Status.NEW);
        newLead.setUser(businessOwner);

        leadRepository.save(newLead);

    }


    private LeadResponseDto toDto(Lead lead) {
        LeadResponseDto leadResponseDto = new LeadResponseDto();
        leadResponseDto.setId(lead.getId());
        leadResponseDto.setName(lead.getName());
        leadResponseDto.setEmail(lead.getEmail());
        leadResponseDto.setPhoneNumber(lead.getPhoneNumber());
        leadResponseDto.setBusinessName(lead.getBusinessName());
        leadResponseDto.setBiggestChallenge(lead.getBiggestChallenge());
        leadResponseDto.setStatus(lead.getStatus().name());
        leadResponseDto.setCreatedAt(lead.getCreatedAt());
        return leadResponseDto;
    }



}

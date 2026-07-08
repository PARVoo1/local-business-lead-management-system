package com.echohype.lead.management.service;

import com.echohype.lead.management.dto.InstagramLeadDto;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class LeadService {

    private final LeadRepository leadRepository;
    private final UserRepository userRepository;
    private static final String OWNER_NOT_FOUND ="Form rejected: Business owner '{}' not found.";


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
            log.error(OWNER_NOT_FOUND, userName);
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
            log.error("Form rejected: Missing mandatory Contacts.");
            return;
        }

        User businessOwner = userRepository.findByUsername(userName);
        if (businessOwner == null) {
            log.error(OWNER_NOT_FOUND, userName);
            return;
        }
        String clientName = value.getContacts().getFirst().getProfile().getName();
        String clientPhone = value.getContacts().getFirst().getWa_id();
        String message = value.getMessages().getFirst().getText().getBody();


       processSocialLead(businessOwner, "WA"+clientPhone, clientName, "WhatsApp DM", message);

    }
    public void saveInstagramLead(InstagramLeadDto dto, String userName) {

        if(dto==null || dto.getEntry()==null||dto.getEntry().isEmpty()) {
            return;
        }
        var messagingList=dto.getEntry().getFirst().getMessaging();

        if (messagingList == null || messagingList.isEmpty()) {
            return;
        }

        var messaging = messagingList.getFirst();



        if (messaging.getMessage() == null || messaging.getMessage().getText() == null) {
            return;
        }

        User businessOwner = userRepository.findByUsername(userName);
        if (businessOwner == null) {
            log.error(OWNER_NOT_FOUND, userName);
            return;
        }
        String instagramId = messaging.getSender().getId();
        String messageBody = messaging.getMessage().getText();

        processSocialLead(businessOwner, "IG ID: " + instagramId, "Instagram User", "Instagram DM", messageBody);

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

    private void processSocialLead(User businessOwner, String platformId, String fallBackName, String businessChannel, String newMessage) {
        LocalDateTime twoMonthsAgo = LocalDateTime.now().minusMonths(2);

        Optional<Lead> existingLeadOpt = leadRepository
                .findFirstByPhoneNumberAndUserAndCreatedAtAfterOrderByCreatedAtDesc(platformId, businessOwner, twoMonthsAgo);

        if (existingLeadOpt.isPresent()) {
            Lead existingLead = existingLeadOpt.get();

            String expandedChallenge = existingLead.getBiggestChallenge() + "\n[Follow-up]: " + newMessage;
            existingLead.setBiggestChallenge(expandedChallenge);

            existingLead.setStatus(Status.NEW);

            leadRepository.save(existingLead);
            log.info("Appended follow-up text to existing lead ID: {} via {}", existingLead.getId(), businessChannel);
        } else {
            Lead newLead = new Lead();
            newLead.setName(fallBackName);
            newLead.setPhoneNumber(platformId);
            newLead.setEmail("N/A");
            newLead.setBusinessName(businessChannel);
            newLead.setBiggestChallenge(newMessage);
            newLead.setStatus(Status.NEW);
            newLead.setUser(businessOwner);

            leadRepository.save(newLead);
            log.info("Registered a brand new sales lead via {}", businessChannel);
        }
    }



}

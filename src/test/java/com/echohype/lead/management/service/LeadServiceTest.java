package com.echohype.lead.management.service;

import com.echohype.lead.management.dto.WebsiteLeadDto;
import com.echohype.lead.management.entity.Lead;
import com.echohype.lead.management.entity.Status;
import com.echohype.lead.management.entity.User;
import com.echohype.lead.management.repository.LeadRepository;
import com.echohype.lead.management.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeadServiceTest {

    @Mock
    private LeadRepository leadRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LeadService leadService;

    @Test
    void validPayload() {

        WebsiteLeadDto dto = new WebsiteLeadDto();
        dto.setName("Rahul Sharma");
        dto.setEmail("rahul@gmail.com");
        dto.setPhone("9876543210");
        dto.setBusinessName("Rahul Stores");
        dto.setBiggestChallenge("Not getting leads from Instagram");

        User mockUser = new User();
        mockUser.setUsername("rahul_stores");
        when(userRepository.findByUsername("rahul_stores")).thenReturn(mockUser);


        leadService.saveLead(dto, "rahul_stores");

        ArgumentCaptor<Lead> captor = ArgumentCaptor.forClass(Lead.class);
        verify(leadRepository, times(1)).save(captor.capture());

        Lead savedLead = captor.getValue();
        assertThat(savedLead.getName()).isEqualTo("Rahul Sharma");
        assertThat(savedLead.getEmail()).isEqualTo("rahul@gmail.com");
        assertThat(savedLead.getPhoneNumber()).isEqualTo("9876543210");
        assertThat(savedLead.getBusinessName()).isEqualTo("Rahul Stores");
        assertThat(savedLead.getBiggestChallenge()).isEqualTo("Not getting leads from Instagram");
        assertThat(savedLead.getStatus()).isEqualTo(Status.NEW);


        assertThat(savedLead.getUser().getUsername()).isEqualTo("rahul_stores");
    }

    @Test
    void invalidPayload_missingFields() {
        WebsiteLeadDto dto = new WebsiteLeadDto();
        dto.setName(null);
        dto.setEmail("rahul@gmail.com");
        dto.setPhone("9876543210");
        dto.setBusinessName("Rahul Stores");
        dto.setBiggestChallenge(null);

        leadService.saveLead(dto, "rahul_stores");
        verify(leadRepository, never()).save(any());
    }

    @Test
    void invalidPayload_userNotFound() {
        WebsiteLeadDto dto = new WebsiteLeadDto();
        dto.setName("Rahul Sharma");
        dto.setEmail("rahul@gmail.com");
        dto.setPhone("9876543210");
        dto.setBusinessName("Rahul Stores");
        dto.setBiggestChallenge("Not getting leads from Instagram");
        when(userRepository.findByUsername("wrong_user_typo")).thenReturn(null);

        leadService.saveLead(dto, "wrong_user_typo");

        verify(leadRepository, never()).save(any());
    }
}
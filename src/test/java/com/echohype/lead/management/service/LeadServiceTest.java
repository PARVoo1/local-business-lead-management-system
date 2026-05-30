package com.echohype.lead.management.service;


import com.echohype.lead.management.dto.FieldDto;
import com.echohype.lead.management.dto.WebhookDto;
import com.echohype.lead.management.entity.Lead;
import com.echohype.lead.management.entity.Status;
import com.echohype.lead.management.repository.LeadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeadServiceTest {

    @Mock
    private LeadRepository leadRepository;
    @InjectMocks
    private LeadService leadService;

    @Test
    void validPayload(){
        FieldDto nameField = new FieldDto();
        nameField.setId("name");
        nameField.setValue("Rahul Sharma");

        FieldDto emailField = new FieldDto();
        emailField.setId("email");
        emailField.setValue("rahul@gmail.com");

        FieldDto phoneField = new FieldDto();
        phoneField.setId("phone");
        phoneField.setValue("9876543210");

        FieldDto businessField = new FieldDto();
        businessField.setId("business_name");
        businessField.setValue("Rahul Stores");

        FieldDto challengeField = new FieldDto();
        challengeField.setId("biggest_challenge");
        challengeField.setValue("Not getting leads from Instagram");

        WebhookDto dto = new WebhookDto();
        dto.setFields(List.of(nameField, emailField, phoneField, businessField, challengeField));

        leadService.saveLead(dto);

        ArgumentCaptor<Lead> captor = ArgumentCaptor.forClass(Lead.class);
        verify(leadRepository, times(1)).save(captor.capture());

        Lead savedLead = captor.getValue();
        assertThat(savedLead.getName()).isEqualTo("Rahul Sharma");
        assertThat(savedLead.getEmail()).isEqualTo("rahul@gmail.com");
        assertThat(savedLead.getPhoneNumber()).isEqualTo("9876543210");
        assertThat(savedLead.getBusinessName()).isEqualTo("Rahul Stores");
        assertThat(savedLead.getBiggestChallenge()).isEqualTo("Not getting leads from Instagram");
        assertThat(savedLead.getStatus()).isEqualTo(Status.NEW);
    }

    @Test
    void invalidPayload(){

        FieldDto nameField = new FieldDto();
        nameField.setId("name");
        nameField.setValue(null);

        FieldDto emailField = new FieldDto();
        emailField.setId("email");
        emailField.setValue("rahul@gmail.com");

        FieldDto phoneField = new FieldDto();
        phoneField.setId("phone");
        phoneField.setValue("9876543210");

        FieldDto businessField = new FieldDto();
        businessField.setId("business_name");
        businessField.setValue("Rahul Stores");

        FieldDto challengeField = new FieldDto();
        challengeField.setId("biggest_challenge");
        challengeField.setValue(null);

        WebhookDto dto = new WebhookDto();
        dto.setFields(List.of(nameField, emailField, phoneField, businessField, challengeField));

        leadService.saveLead(dto);

        verify(leadRepository, never()).save(any());
    }
}

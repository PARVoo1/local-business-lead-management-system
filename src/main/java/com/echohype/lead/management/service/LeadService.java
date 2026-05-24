package com.echohype.lead.management.service;

import com.echohype.lead.management.entity.Lead;
import com.echohype.lead.management.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadRepository leadRepository;

    public void saveLead(Lead lead) {
        leadRepository.save(lead);
    }

}

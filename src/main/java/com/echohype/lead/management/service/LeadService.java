package com.echohype.lead.management.service;

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

    public List<Lead> search(Status status, Integer month) {
        Specification<Lead> filter = Specification
                .allOf(LeadSpecification.hasStatus(status)
                        ,LeadSpecification.hasMonth(month));
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        return leadRepository.findAll(filter, sort);
    }


    public void saveLead(Lead lead) {
        leadRepository.save(lead);
    }



}

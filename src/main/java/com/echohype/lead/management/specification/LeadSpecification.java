package com.echohype.lead.management.specification;

import com.echohype.lead.management.entity.Lead;
import com.echohype.lead.management.entity.Status;
import org.springframework.data.jpa.domain.Specification;

public class LeadSpecification {

    public static Specification<Lead> hasStatus(Status status) {
        if(status == null) {
            return null;
        }
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }


}

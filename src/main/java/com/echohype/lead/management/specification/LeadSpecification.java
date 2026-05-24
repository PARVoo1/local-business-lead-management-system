package com.echohype.lead.management.specification;

import com.echohype.lead.management.entity.Lead;
import com.echohype.lead.management.entity.Status;
import org.springframework.data.jpa.domain.Specification;

public class LeadSpecification {

    public static Specification<Lead> hasStatus(Status status) {
        if(status == null) {
            return Specification.unrestricted();
        }
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Lead> hasMonth(Integer month) {
        if(month == null || month < 1 || month > 12) {
            return Specification.unrestricted();
        }
        return (root, criteriaQuery, criteriaBuilder) ->{
            return criteriaBuilder.equal(criteriaBuilder.function("date_part",Integer.class,criteriaBuilder.literal("month"),root.get("createdAt")),month
            );
        };
    }


}

package com.echohype.lead.management.repository;

import com.echohype.lead.management.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface LeadRepository extends JpaRepository<Lead,Long>, JpaSpecificationExecutor<Lead> {

}

package com.echohype.lead.management.repository;

import com.echohype.lead.management.entity.Lead;
import com.echohype.lead.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.Optional;


public interface LeadRepository extends JpaRepository<Lead,Long>, JpaSpecificationExecutor<Lead> {
    Optional<Lead> findFirstByPhoneNumberAndUserAndCreatedAtAfterOrderByCreatedAtDesc(
            String phoneNumber,
            User user,
            LocalDateTime cutoffDate
    );
}

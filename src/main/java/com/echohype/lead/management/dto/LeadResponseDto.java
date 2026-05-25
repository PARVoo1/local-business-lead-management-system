package com.echohype.lead.management.dto;

import com.echohype.lead.management.entity.Status;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Data
public class LeadResponseDto {
    @Column(length = 50, nullable = false)
    private String name;

    private String email;
    private String phoneNumber;
    private String businessName;
    private String biggestChallenge;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status=Status.NEW;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

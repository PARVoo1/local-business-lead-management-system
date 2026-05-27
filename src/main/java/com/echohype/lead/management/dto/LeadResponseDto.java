package com.echohype.lead.management.dto;

import lombok.Data;


import java.time.LocalDateTime;


@Data
public class LeadResponseDto {
    private Long id;
    private String name;

    private String email;
    private String phoneNumber;
    private String businessName;
    private String biggestChallenge;

    private String status;
    private LocalDateTime createdAt;
}

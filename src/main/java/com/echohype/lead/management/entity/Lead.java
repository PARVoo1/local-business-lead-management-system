package com.echohype.lead.management.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity(name = "leads")
@Data
public class Lead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50, nullable = false)
    private String name;

    private String email;

    private String phoneNumber;

    private String businessName;

    private String biggestChallenge;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status=Status.NEW;
    @Column(nullable = false)
    private LocalDateTime createdAt;
}

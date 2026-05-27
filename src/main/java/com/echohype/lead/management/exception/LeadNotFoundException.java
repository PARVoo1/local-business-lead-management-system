package com.echohype.lead.management.exception;


public class LeadNotFoundException extends RuntimeException {
    public LeadNotFoundException(Long id) {
        super("Lead with id " + id+ " not found");
    }
}

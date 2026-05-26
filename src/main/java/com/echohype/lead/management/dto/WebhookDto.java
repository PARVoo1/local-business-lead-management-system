package com.echohype.lead.management.dto;

import lombok.Data;

import java.util.List;

@Data
public class WebhookDto {
    private String form_id;
    private String form_name;
    private List<FieldDto> fields;
}

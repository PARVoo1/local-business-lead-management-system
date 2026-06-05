package com.echohype.lead.management.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstagramLeadDto {

    private String object;
    private List<Entry> entry;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Entry {
        private List<Messaging> messaging;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Messaging {
        private Sender sender;
        private Message message;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sender {
        private String id;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Message {
        private String text;
    }
}
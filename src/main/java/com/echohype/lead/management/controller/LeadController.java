package com.echohype.lead.management.controller;

import com.echohype.lead.management.dto.LeadResponseDto;
import com.echohype.lead.management.dto.WebsiteLeadDto;
import com.echohype.lead.management.entity.Status;
import com.echohype.lead.management.exception.LeadNotFoundException;
import com.echohype.lead.management.service.LeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
public class LeadController {


    private final LeadService leadService;
    @Value("${webhook.secret}")
    private String secret;


    @PostMapping("/website/{userName}")
    public ResponseEntity<Void>saveLead(
            @PathVariable String userName,
            @RequestBody WebsiteLeadDto dto,
            @RequestHeader("X-Webhook-Secret") String secretKey){

        if(secret.equals(secretKey)){
            leadService.saveLead(dto,userName);
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.status(403).build();
        }

    }


    @GetMapping
    public List<LeadResponseDto> getLeads(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer month,
            Principal principal) {

        String loggedInUsername = principal.getName();

        if(status == null) {
            return leadService.search(null,month,loggedInUsername);
        }
        else{
            try {
                Status status1 =Status.valueOf(status.toUpperCase());
                return leadService.search(status1, month,loggedInUsername);
            }catch (IllegalArgumentException e) {
                return  List.of();
            }
        }

    }
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void>updateLead(
            @RequestBody Map<String, String> body,
            @PathVariable Long id,
            Principal principal) {

        try{
            Status status1 =Status.valueOf(body.get("status").toUpperCase());
            leadService.updateStatus(id,status1,principal.getName());
            return ResponseEntity.ok().build();
        }catch (IllegalArgumentException e) {
            return  ResponseEntity.badRequest().build();
        }catch (LeadNotFoundException e) {
            return ResponseEntity.notFound().build();
        }catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        }
    }

}

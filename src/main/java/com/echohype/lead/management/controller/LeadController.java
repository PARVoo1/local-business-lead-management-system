package com.echohype.lead.management.controller;

import com.echohype.lead.management.dto.LeadResponseDto;
import com.echohype.lead.management.entity.Status;
import com.echohype.lead.management.exception.LeadNotFoundException;
import com.echohype.lead.management.service.LeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;


    @GetMapping
    public List<LeadResponseDto> getLeads(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer month) {

        if(status == null) {
            return leadService.search(null,month);
        }
        else{
            try {
                Status status1 =Status.valueOf(status.toUpperCase());
                return leadService.search(status1, month);
            }catch (IllegalArgumentException e) {
                return  List.of();
            }
        }

    }
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void>updateLead(
            @RequestBody Map<String, String> body,
            @PathVariable Long id){

        try{
            Status status1 =Status.valueOf(body.get("status").toUpperCase());
            leadService.updateStatus(id,status1);
            return ResponseEntity.ok().build();
        }catch (IllegalArgumentException e) {
            return  ResponseEntity.badRequest().build();
        }catch (LeadNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}

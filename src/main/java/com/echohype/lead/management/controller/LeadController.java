package com.echohype.lead.management.controller;

import com.echohype.lead.management.dto.LeadResponseDto;
import com.echohype.lead.management.entity.Status;
import com.echohype.lead.management.service.LeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}

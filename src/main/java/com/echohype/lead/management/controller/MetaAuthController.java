package com.echohype.lead.management.controller;

import com.echohype.lead.management.service.MetaIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/meta")
@RequiredArgsConstructor
public class MetaAuthController {

    private final MetaIntegrationService metaIntegrationService;

    @PostMapping("/connect")
    public ResponseEntity<String> connectMeta(@RequestBody Map<String, String> payload, Principal principal) {
        String code = payload.get("code");
        String loggedInUsername = principal.getName();

        metaIntegrationService.processMetaCode(code, loggedInUsername);

        return ResponseEntity.ok("Meta successfully connected to your account!");
    }
}
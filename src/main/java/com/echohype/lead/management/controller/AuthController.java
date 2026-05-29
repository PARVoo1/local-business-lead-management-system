package com.echohype.lead.management.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Value("${DASHBOARD_PASSWORD}")
    private String dashboardPassword;


    @PostMapping("/auth")
    public ResponseEntity<String> auth(@RequestBody Map<String, String> body) {
        if (dashboardPassword.equals(body.get("password"))) {
            return ResponseEntity.ok("authenticated");
        }
        return ResponseEntity.status(401).build();
    }
}

package com.echohype.lead.management.controller;


import com.echohype.lead.management.dto.UserDto;
import com.echohype.lead.management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;


    @PostMapping("/auth")
    public ResponseEntity<Map<String,String>> auth(@RequestBody UserDto userDto){
        String jwt = userService.userLogin(userDto);
        return ResponseEntity.ok(Map.of("token", jwt));
    }
}

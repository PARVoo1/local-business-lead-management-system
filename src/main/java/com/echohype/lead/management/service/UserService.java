package com.echohype.lead.management.service;

import com.echohype.lead.management.dto.UserDto;
import com.echohype.lead.management.entity.User;
import com.echohype.lead.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void userLogin(@RequestBody UserDto user) {

        User byUsername = userRepository.findByUsername(user.getUsername());
        if(!passwordEncoder.matches(user.getPassword(), byUsername.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

    }
}

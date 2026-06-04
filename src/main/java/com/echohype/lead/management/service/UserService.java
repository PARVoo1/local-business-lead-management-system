package com.echohype.lead.management.service;

import com.echohype.lead.management.dto.UserDto;
import com.echohype.lead.management.entity.User;
import com.echohype.lead.management.repository.UserRepository;
import com.echohype.lead.management.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public String userLogin(UserDto userDto) {

        User user = userRepository.findByUsername(userDto.getUsername());
        if(user==null || !passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        return jwtUtil.generateToken(userDto.getUsername());

    }
}

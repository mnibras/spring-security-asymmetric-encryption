package com.nibras.app.auth.impl;

import com.nibras.app.auth.AuthenticationService;
import com.nibras.app.auth.request.AuthenticationRequest;
import com.nibras.app.auth.request.RefreshRequest;
import com.nibras.app.auth.request.RegistrationRequest;
import com.nibras.app.auth.response.AuthenticationResponse;
import com.nibras.app.exception.BusinessException;
import com.nibras.app.role.Role;
import com.nibras.app.role.RoleRepository;
import com.nibras.app.security.JwtService;
import com.nibras.app.user.User;
import com.nibras.app.user.UserMapper;
import com.nibras.app.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.nibras.app.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public void register(RegistrationRequest request) {
        checkUserEmail(request.getEmail());
        checkUserPhoneNumber(request.getPhoneNumber());
        checkPasswords(request.getPassword(), request.getConfirmPassword());

        final Role role = this.roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new EntityNotFoundException("Role user does not exist"));
        final List<Role> roles = new ArrayList<>();
        roles.add(role);

        final User user = this.userMapper.toUser(request);
        user.setRoles(roles);

        this.userRepository.save(user);
        log.debug("Created User: {}", user);

        final List<User> users = new ArrayList<>();
        users.add(user);
        role.setUsers(users);

        this.roleRepository.save(role);
    }

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        final Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        final User user = (User) authentication.getPrincipal();

        final String accessToken = jwtService.generateAccessToken(user.getUsername());
        final String refreshToken = jwtService.generateRefreshToken(user.getUsername());
        final String tokenType = "Bearer";

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken) // todo send it via cookie
                .tokenType(tokenType)
                .build();
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshRequest req) {
        final String newAccessToken = this.jwtService.refreshAccessToken(req.getRefreshToken());
        final String tokenType = "Bearer";
        return AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(req.getRefreshToken())
                .tokenType(tokenType)
                .build();
    }

    private void checkUserEmail(final String email) {
        final boolean emailExists = this.userRepository.existsByEmailIgnoreCase(email);
        if (emailExists) {
            throw new BusinessException(EMAIL_ALREADY_EXISTS);
        }
    }

    private void checkPasswords(final String password,
                                final String confirmPassword) {
        if (password == null || !password.equals(confirmPassword)) {
            throw new BusinessException(PASSWORD_MISMATCH);
        }
    }

    private void checkUserPhoneNumber(final String phoneNumber) {
        final boolean phoneNumberExists = this.userRepository.existsByPhoneNumber(phoneNumber);
        if (phoneNumberExists) {
            throw new BusinessException(PHONE_ALREADY_EXISTS);
        }
    }
}

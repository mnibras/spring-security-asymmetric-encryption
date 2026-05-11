package com.nibras.app.user.impl;

import com.nibras.app.exception.BusinessException;
import com.nibras.app.user.User;
import com.nibras.app.user.UserMapper;
import com.nibras.app.user.UserRepository;
import com.nibras.app.user.UserService;
import com.nibras.app.user.request.ChangePasswordRequest;
import com.nibras.app.user.request.ProfileUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.nibras.app.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return this.userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Override
    public void updateProfileInfo(final ProfileUpdateRequest request, final String userId) {
        final User savedUser = getSavedUser(userId);
        this.userMapper.mergeUserInfo(savedUser, request);
        this.userRepository.save(savedUser);
    }

    @Override
    public void changePassword(final ChangePasswordRequest req, final String userId) {
        if (!req.getNewPassword().equals(req.getConfirmNewPassword())) {
            throw new BusinessException(CHANGE_PASSWORD_MISMATCH);
        }

        final User savedUser = getSavedUser(userId);

        if (!this.passwordEncoder.matches(req.getCurrentPassword(), savedUser.getPassword())) {
            throw new BusinessException(INVALID_CURRENT_PASSWORD);
        }

        final String encoded = this.passwordEncoder.encode(req.getNewPassword());
        savedUser.setPassword(encoded);

        this.userRepository.save(savedUser);
    }

    @Override
    public void deactivateAccount(final String userId) {
        final User user = getSavedUser(userId);

        if (!user.isEnabled()) {
            throw new BusinessException(ACCOUNT_ALREADY_DEACTIVATED);
        }

        user.setEnabled(false);
        this.userRepository.save(user);
    }

    @Override
    public void reactivateAccount(final String userId) {
        final User user = getSavedUser(userId);

        if (user.isEnabled()) {
            throw new BusinessException(ACCOUNT_ALREADY_DEACTIVATED);
        }

        user.setEnabled(true);
        this.userRepository.save(user);
    }

    @Override
    public void deleteAccount(final String userId) {
        // this method need the rest of the entities
        // the logic is just to schedule a profile for deletion
        // and then a scheduled job will pick up the profiles and delete everything
    }

    private User getSavedUser(String userId) {
        return this.userRepository.findById(userId).orElseThrow(() -> new BusinessException(USER_NOT_FOUND));
    }

}

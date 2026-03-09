package com.revpay.service.impl;

import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.revpay.dto.UpdateProfileRequest;
import com.revpay.model.BusinessProfile;
import com.revpay.model.User;
import com.revpay.model.enums.UserType;
import com.revpay.repository.BusinessProfileRepository;
import com.revpay.repository.UserRepository;
import com.revpay.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LogManager.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final BusinessProfileRepository businessProfileRepository;

    public UserServiceImpl(UserRepository userRepository,
                           BusinessProfileRepository businessProfileRepository) {
        this.userRepository = userRepository;
        this.businessProfileRepository = businessProfileRepository;
    }

    @Override
    @Transactional
    public void updateProfile(UpdateProfileRequest request) {

        log.info("Updating profile for userId={}", request.getUserId());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> {
                    log.warn("Profile update failed: user not found userId={}", request.getUserId());
                    return new IllegalArgumentException("User not found");
                });

        if (request.getFullName() != null)
            user.setFullName(request.getFullName());

        if (request.getEmail() != null)
            user.setEmail(request.getEmail());

        if (request.getPhone() != null)
            user.setPhone(request.getPhone());

        userRepository.save(user);

        log.info("User basic profile updated userId={}", user.getUserId());

        // Update business profile if user is BUSINESS
        if (user.getUserType() == UserType.BUSINESS) {

            log.info("Updating business profile for userId={}", user.getUserId());

            BusinessProfile profile =
                    businessProfileRepository.findByUser(user)
                            .orElseThrow(() -> {
                                log.warn("Business profile not found userId={}", user.getUserId());
                                return new IllegalStateException("Business profile not found");
                            });

            if (request.getBusinessName() != null)
                profile.setBusinessName(request.getBusinessName());

            if (request.getBusinessType() != null)
                profile.setBusinessType(request.getBusinessType());

            if (request.getAddress() != null)
                profile.setAddress(request.getAddress());

            businessProfileRepository.save(profile);

            log.info("Business profile updated userId={}", user.getUserId());
        }
    }
}
package com.revpay.service.impl;

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

    private final UserRepository userRepository;
    private final BusinessProfileRepository businessProfileRepository;

    public UserServiceImpl(UserRepository userRepository,
                           BusinessProfileRepository businessProfileRepository) {
        this.userRepository = userRepository;
        this.businessProfileRepository = businessProfileRepository;
    }

    @Override
    public void updateProfile(UpdateProfileRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (request.getFullName() != null)
            user.setFullName(request.getFullName());

        if (request.getEmail() != null)
            user.setEmail(request.getEmail());

        if (request.getPhone() != null)
            user.setPhone(request.getPhone());

        userRepository.save(user);

        // Update business profile if user is BUSINESS
        if (user.getUserType() == UserType.BUSINESS) {

            BusinessProfile profile =
                    businessProfileRepository.findByUser(user)
                            .orElseThrow(() -> new IllegalStateException("Business profile not found"));

            if (request.getBusinessName() != null)
                profile.setBusinessName(request.getBusinessName());

            if (request.getBusinessType() != null)
                profile.setBusinessType(request.getBusinessType());

            if (request.getAddress() != null)
                profile.setAddress(request.getAddress());

            businessProfileRepository.save(profile);
        }
    }
}
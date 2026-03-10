package com.revpay.service;

import com.revpay.dto.UpdateProfileRequest;
import com.revpay.model.User;

public interface UserService {

    void updateProfile(UpdateProfileRequest request);

    User getUserById(Long userId);
}
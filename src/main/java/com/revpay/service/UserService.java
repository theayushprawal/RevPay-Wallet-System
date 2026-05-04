package com.revpay.service;

import com.revpay.dto.request.UpdateProfileRequest;
import com.revpay.dto.response.UserResponse;
import com.revpay.model.User;

public interface UserService {

    void updateProfile(UpdateProfileRequest request);

    User getUserById(Long userId);

    UserResponse getUserResponseById(Long userId);
}
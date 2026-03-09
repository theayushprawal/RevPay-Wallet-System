package com.revpay.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revpay.dto.ApiResponse;
import com.revpay.dto.UpdateProfileRequest;
import com.revpay.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * UPDATE PROFILE
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<Void>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request) {

        userService.updateProfile(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Profile updated successfully",
                        null
                )
        );
    }
}
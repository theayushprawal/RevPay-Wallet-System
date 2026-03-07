package com.revpay.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> updateProfile(
            @RequestBody UpdateProfileRequest request) {

        userService.updateProfile(request);

        return ResponseEntity.ok("Profile updated successfully");
    }
}
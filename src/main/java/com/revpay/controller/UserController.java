package com.revpay.controller;

import com.revpay.dto.UserResponse;
import com.revpay.model.User;
import com.revpay.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.revpay.dto.ApiResponse;
import com.revpay.dto.UpdateProfileRequest;
import com.revpay.service.UserService;

import java.util.Optional;

@RestController
@RequestMapping("/users")
@PreAuthorize("hasAnyAuthority('PERSONAL', 'BUSINESS')") // Must be a RevPay user
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    /**
     * UPDATE PROFILE
     */
    // IDOR protection using the userId inside the request body
    @PreAuthorize("hasAnyAuthority('PERSONAL', 'BUSINESS') and @securityGuard.isUserMatching(authentication, #request.userId)")
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<Void>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request) {

        userService.updateProfile(request);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Profile updated successfully", null)
        );
    }

    /**
     * GET PROFILE
     */
    // IDOR protection - a user can only view their own detailed profile object

    @PreAuthorize("hasAnyAuthority('PERSONAL', 'BUSINESS') and @securityGuard.isUserMatching(authentication, #id)")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserProfile(@PathVariable Long id) {

        UserResponse response = userService.getUserResponseById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Profile loaded successfully", response)
        );
    }

    /**
     * RESOLVE USER BY ID, EMAIL, OR PHONE
     */
    // No IDOR check here because we NEED to look up people we don't own to pay them.
    // The @PreAuthorize at the top ensures only logged-in users can use this tool.
    @GetMapping("/lookup")
    public ResponseEntity<ApiResponse<Long>> lookupUser(@RequestParam String identifier) {

        // 1. Try treating it as a numeric Account ID first
        try {
            Long id = Long.parseLong(identifier);
            if (userRepository.existsById(id)) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Found by ID", id));
            }
        } catch (NumberFormatException e) {
            // Not a number, moving on to Email/Phone check
        }

        // 2. Try treating it as an Email or Phone
        Optional<User> user = userRepository.findByEmailOrPhone(identifier, identifier);

        if (user.isPresent()) {
            return ResponseEntity.ok(new ApiResponse<>(true, "Found by alias", user.get().getUserId()));
        }

        return ResponseEntity.badRequest().body(new ApiResponse<>(false, "User not found with that Email, Phone, or ID", null));
    }
}
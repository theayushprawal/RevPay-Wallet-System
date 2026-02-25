package com.revpay.dto;

import java.util.List;

import com.revpay.model.enums.UserType;

public class RegisterRequest {

    // ===== BASIC USER DETAILS =====
    private String fullName;
    private String email;
    private String phone;
    private String password;
    private UserType userType;

    // ===== SECURITY =====
    private List<SecurityQuestionRequest> securityQuestions;

    // ===== BUSINESS DETAILS (only if BUSINESS user) =====
    private String businessName;
    private String businessType;
    private String panNumber;
    private String address;

    // ===== CONSTRUCTORS =====
    public RegisterRequest() {
    }

    // ===== GETTERS & SETTERS =====

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public List<SecurityQuestionRequest> getSecurityQuestions() {
        return securityQuestions;
    }

    public void setSecurityQuestions(List<SecurityQuestionRequest> securityQuestions) {
        this.securityQuestions = securityQuestions;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
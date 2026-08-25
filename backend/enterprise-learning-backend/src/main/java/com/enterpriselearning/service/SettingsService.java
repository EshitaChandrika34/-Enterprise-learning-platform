package com.enterpriselearning.service;

import com.enterpriselearning.dto.request.ChangePasswordRequest;
import com.enterpriselearning.dto.request.ProfileUpdateRequest;
import com.enterpriselearning.dto.response.UserResponse;

public interface SettingsService {
    UserResponse getProfile(String currentUserEmail);
    UserResponse updateProfile(ProfileUpdateRequest request, String currentUserEmail);
    void changePassword(ChangePasswordRequest request, String currentUserEmail);
}

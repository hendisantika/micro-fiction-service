package com.grayseal.microfictionapi;

import com.grayseal.microfictionapi.model.UserRegistrationRequest;

public class Utils {

    public static boolean isValidRegistrationRequest(UserRegistrationRequest registrationRequest) {
        if (registrationRequest == null) {
            return false;
        }
        if (registrationRequest.getEmail() == null || !isValidEmail(registrationRequest.getEmail())) {
            return false;
        }
        return registrationRequest.getPassword() != null;
    }

    private static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }
}

package com.grayseal.microfictionapi.util;

import com.grayseal.microfictionapi.model.User;

public class TextUtils {

    public static boolean isValidRegistrationRequest(User user) {
        if (user == null) {
            return false;
        }
        if (user.getEmail() == null || !isValidEmail(user.getEmail())) {
            return false;
        }
        return user.getPassword() != null;
    }

    private static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }
}

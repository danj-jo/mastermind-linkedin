package com.example.mastermind.utils;

import com.example.mastermind.customExceptions.UnauthenticatedUserException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class PlayerUtils {
    
    /**
     * Gets the current authenticated user's username.
     * 
     * This utility method extracts the username from the current security context,
     * providing a centralized way to get the authenticated user across the application.
     * 
     * @return the username of the currently authenticated user
     * @throws UnauthenticatedUserException if no user is authenticated or the authentication is invalid
     */
    public static String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthenticatedUserException("User is not authenticated.");
        }
        return auth.getName();
    }
}

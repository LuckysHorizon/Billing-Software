package com.grocerypos.util;

import com.grocerypos.model.User;

/**
 * Session manager for handling current user session
 */
public class SessionManager {
    private static User currentUser;
    
    /**
     * Set the current user
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }
    
    /**
     * Get the current user
     */
    public static User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Clear the current session
     */
    public static void clearSession() {
        currentUser = null;
    }
    
    /**
     * Check if user is logged in
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Check if current user has admin role
     */
    public static boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == User.Role.ADMIN;
    }
    
    /**
     * Check if current user has manager role or higher
     */
    public static boolean isManager() {
        return currentUser != null && 
               (currentUser.getRole() == User.Role.ADMIN || currentUser.getRole() == User.Role.MANAGER);
    }
    
    /**
     * Get current user ID
     */
    public static int getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }
    
    /**
     * Get current user name
     */
    public static String getCurrentUserName() {
        return currentUser != null ? currentUser.getFullName() : "Unknown";
    }
    
    /**
     * Get current user role
     */
    public static String getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole().name() : "UNKNOWN";
    }
}

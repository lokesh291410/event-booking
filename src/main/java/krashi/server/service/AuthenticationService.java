package krashi.server.service;

import krashi.server.entity.UserInfo;

public interface AuthenticationService {
    
    /**
     * Gets the currently authenticated user.
     * 
     * @return UserInfo of the authenticated user
     * @throws AccessDeniedException if user is not authenticated
     * @throws ResourceNotFoundException if user is not found
     */
    UserInfo getCurrentUser();
    
    /**
     * Gets the currently authenticated admin user.
     * 
     * @return UserInfo of the authenticated admin
     * @throws AccessDeniedException if user is not authenticated or not an admin
     * @throws ResourceNotFoundException if user is not found
     */
    UserInfo getCurrentAdmin();
    
    /**
     * Gets the ID of the currently authenticated user.
     * 
     * @return User ID
     * @throws AccessDeniedException if user is not authenticated
     */
    Long getCurrentUserId();
    
    /**
     * Gets the ID of the currently authenticated admin.
     * 
     * @return Admin user ID
     * @throws AccessDeniedException if user is not authenticated or not an admin
     */
    Long getCurrentAdminId();
    
    /**
     * Gets the username of the currently authenticated user.
     * 
     * @return Username or null if not authenticated
     */
    String getCurrentUsername();
    
    /**
     * Checks if the currently authenticated user is an admin.
     * 
     * @return true if current user is admin, false otherwise
     */
    boolean isCurrentUserAdmin();
    
    /**
     * Checks if the given user ID matches the currently authenticated user.
     * 
     * @param userId User ID to check
     * @return true if the given user ID matches current user, false otherwise
     */
    boolean isCurrentUser(Long userId);
}

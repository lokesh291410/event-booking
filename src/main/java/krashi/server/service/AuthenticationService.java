package krashi.server.service;

import krashi.server.entity.UserInfo;

public interface AuthenticationService {
    
    UserInfo getCurrentUser();
    UserInfo getCurrentAdmin();
    Long getCurrentUserId();
    Long getCurrentAdminId();
    String getCurrentUsername();
    boolean isCurrentUserAdmin();
    boolean isCurrentUser(Long userId);
}

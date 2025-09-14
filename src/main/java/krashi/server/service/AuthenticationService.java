package krashi.server.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import krashi.server.entity.UserInfo;
import krashi.server.exception.AccessDeniedException;
import krashi.server.exception.ResourceNotFoundException;
import krashi.server.repository.UserInfoRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthenticationService {
    
    private final UserInfoRepository userInfoRepository;
    
    public UserInfo getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User not authenticated");
        }
        
        String username = authentication.getName();
        return userInfoRepository.findByUserName(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }
    
    public UserInfo getCurrentAdmin() {
        UserInfo user = getCurrentUser();
        
        if (!"ROLE_ADMIN".equals(user.getRole())) {
            throw new AccessDeniedException("Only admins can perform this operation");
        }
        
        return user;
    }
    
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
    
    public Long getCurrentAdminId() {
        return getCurrentAdmin().getId();
    }
    
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }
    
    public boolean isCurrentUserAdmin() {
        try {
            UserInfo user = getCurrentUser();
            return "ROLE_ADMIN".equals(user.getRole());
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isCurrentUser(Long userId) {
        try {
            return getCurrentUserId().equals(userId);
        } catch (Exception e) {
            return false;
        }
    }
}

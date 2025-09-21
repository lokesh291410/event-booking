package krashi.server.service.serviceImpl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import krashi.server.entity.UserInfo;
import krashi.server.exception.AccessDeniedException;
import krashi.server.exception.ResourceNotFoundException;
import krashi.server.repository.UserInfoRepository;
import krashi.server.service.AuthenticationService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    
    private final UserInfoRepository userInfoRepository;
    
    @Override
    public UserInfo getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User not authenticated");
        }
        
        String username = authentication.getName();
        return userInfoRepository.findByUserName(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }
    
    @Override
    public UserInfo getCurrentAdmin() {
        UserInfo user = getCurrentUser();
        
        if (!"ROLE_ADMIN".equals(user.getRole())) {
            throw new AccessDeniedException("Only admins can perform this operation");
        }
        
        return user;
    }
    
    @Override
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
    
    @Override
    public Long getCurrentAdminId() {
        return getCurrentAdmin().getId();
    }
    
    @Override
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }
    
    @Override
    public boolean isCurrentUserAdmin() {
        try {
            UserInfo user = getCurrentUser();
            return "ROLE_ADMIN".equals(user.getRole());
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean isCurrentUser(Long userId) {
        try {
            return getCurrentUserId().equals(userId);
        } catch (Exception e) {
            return false;
        }
    }
}
package krashi.server.service.serviceImpl;

import krashi.server.entity.UserInfo;
import krashi.server.repository.EventRepository;
import krashi.server.repository.UserInfoRepository;
import krashi.server.service.SignUpService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SignUpServiceImpl implements SignUpService {


    UserInfoRepository userInfoRepository;
    EventRepository eventRepository;

    PasswordEncoder passwordEncoder;

    public SignUpServiceImpl(UserInfoRepository userInfoRepository, PasswordEncoder passwordEncoder, EventRepository eventRepository) {
        this.userInfoRepository = userInfoRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventRepository = eventRepository;
    }

    @Override
    public ResponseEntity<?> signUp(String username, String name, String email, String password, String role) {
        UserInfo user = new UserInfo();
        user.setUserName(username);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);

        userInfoRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    @Override
    public ResponseEntity<?> getAllEvents() {
        return ResponseEntity.ok(eventRepository.findAll());
    }
}

package krashi.server.controller;

import krashi.server.dto.UserInfoDto;
import krashi.server.service.SignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SignupController {

    @Autowired
    SignUpService signUpService;

    // @GetMapping("/hello")
    // public String hello() {
    //     return "Hello";
    // }

    // @GetMapping("/secure")
    // @PreAuthorize("hasRole('ADMIN')")
    // public String secure() {
    //     return "Secure";
    // }

    // @GetMapping("/")
    // public String home() {
    //     return "Home";
    // }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody UserInfoDto userInfoDto) {
        return signUpService.signUp(
                userInfoDto.getUsername(),
                userInfoDto.getName(),
                userInfoDto.getEmail(),
                userInfoDto.getPassword(),
                userInfoDto.getRole()
        );
    }

    @GetMapping("/events")
    public ResponseEntity<?> getAllEvents() {
        return signUpService.getAllEvents();
    }
}

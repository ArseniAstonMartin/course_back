package udemy.clone.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import udemy.clone.model.authentication.JwtResponseDto;
import udemy.clone.model.authentication.LoginRequestDto;
import udemy.clone.model.authentication.RegistrationDto;
import udemy.clone.model.authentication.UserInfoDto;
import udemy.clone.service.AuthenticationService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(authenticationService.login(loginRequestDto));
    }

    @PostMapping("/registration")
    public ResponseEntity<JwtResponseDto> registration(@RequestBody RegistrationDto registrationRequestDto) {
        return ResponseEntity.ok(authenticationService.registration(registrationRequestDto));
    }

    @GetMapping("/info")
    public ResponseEntity<UserInfoDto> getUserInfo() {
        return ResponseEntity.ok(authenticationService.getUserInfo());
    }
}

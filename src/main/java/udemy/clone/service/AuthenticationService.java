package udemy.clone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import udemy.clone.config.JwtProvider;
import udemy.clone.exceptions.AuthException;
import udemy.clone.mapper.JwtMapper;
import udemy.clone.model.User;
import udemy.clone.model.authentication.JwtResponseDto;
import udemy.clone.model.authentication.LoginRequestDto;
import udemy.clone.model.authentication.RegistrationDto;
import udemy.clone.model.authentication.UserInfoDto;
import udemy.clone.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final JwtMapper jwtMapper;
    private final PasswordEncoder passwordEncoder;

    @PreAuthorize("isAnonymous()")
    public JwtResponseDto login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.email())
                .orElseThrow(() -> new AuthException("Account doesn't exist"));
        if (!passwordEncoder.matches(loginRequestDto.password(), user.getPassword())) {
            throw new AuthException("Wrong password");
        }
        final String accessToken = jwtProvider.generateAccessToken(user);
        return jwtMapper.toDtoCustom(accessToken, user);
    }

    @PreAuthorize("isAnonymous()")
    public JwtResponseDto registration(RegistrationDto registrationDto) {
        Optional<User> user = userRepository.findByEmail(registrationDto.email());
        if (user.isPresent()) {
            throw new AuthException("Account with email = " + registrationDto.email() + " already exist");
        }
        User newUser = User.builder()
                .email(registrationDto.email())
                .password(passwordEncoder.encode(registrationDto.password()))
                .name(registrationDto.name())
                .role(User.Role.valueOf(registrationDto.role()))
                .build();
        userRepository.save(newUser);
        final String accessToken = jwtProvider.generateAccessToken(newUser);
        return jwtMapper.toDtoCustom(accessToken, newUser);
    }

    @PreAuthorize("isAuthenticated()")
    public UserInfoDto getUserInfo() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new UserInfoDto(user.getName(), user.getEmail());
    }
}

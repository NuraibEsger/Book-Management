package az.idtech.bookmanagement.services;

import az.idtech.bookmanagement.dao.entities.UserEntity;
import az.idtech.bookmanagement.dao.repository.UserRepository;
import az.idtech.bookmanagement.model.enums.Role;
import az.idtech.bookmanagement.model.requests.LoginRequest;
import az.idtech.bookmanagement.model.responses.LoginResponse;
import az.idtech.bookmanagement.model.responses.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenStorageService tokenStorageService;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CacheManager cacheManager;

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserEntity userEntity = (UserEntity) authentication.getPrincipal();
        String accessToken = jwtService.generateAccessToken(userEntity);
        String refreshToken = jwtService.generateRefreshToken(userEntity);

        tokenStorageService.storeAccessToken(userEntity.getUsername(), accessToken);
        tokenStorageService.storeRefreshToken(userEntity.getUsername(), refreshToken);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public void register (RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }

        UserEntity user;
        Long userCount;
        var userCountCache = cacheManager.getCache("users").get("userCount", Long.class);
        if (userCountCache == null) {
            userCount = userRepository.findUserCount();
            cacheManager.getCache("users").put("userCount", userCount);
        }

        if (userCountCache == null) {
            user = UserEntity.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .roles(Set.of(Role.ROLE_ADMIN,  Role.ROLE_USER))
                    .build();
        }
        else {
            user = UserEntity.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .roles(Set.of(Role.ROLE_USER))
                    .build();
        }

        userRepository.save(user);
    }
}

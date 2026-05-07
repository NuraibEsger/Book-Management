package az.idtech.bookmanagement.services;

import az.idtech.bookmanagement.dao.entities.UserEntity;
import az.idtech.bookmanagement.dao.repository.UserRepository;
import az.idtech.bookmanagement.model.requests.LoginRequest;
import az.idtech.bookmanagement.model.responses.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
}

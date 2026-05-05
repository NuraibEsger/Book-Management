package az.idtech.bookmanagement.services;

import az.idtech.bookmanagement.dao.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenStorageService {
    private static final String ACCESS_TOKEN_PREFIX = "access_token:";
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    private final StringRedisTemplate redisTemplate;
    private final JwtService jwtService;

    public void storeAccessToken(String userName, String token) {
        String key = ACCESS_TOKEN_PREFIX + userName;
        redisTemplate.opsForValue().set(key, token, jwtService.getAccessTokenValiditySeconds(), TimeUnit.MINUTES);
    }

    public void storeRefreshToken(String userName, String token) {
        String key = REFRESH_TOKEN_PREFIX + userName;
        redisTemplate.opsForValue().set(key, token, jwtService.getRefreshTokenValiditySeconds(), TimeUnit.MINUTES);
    }

    public boolean isAccessTokenValid(String username, String token) {
        String key = ACCESS_TOKEN_PREFIX + username;
        String storedToken = redisTemplate.opsForValue().get(key);
        return token.equals(storedToken);
    }

    public boolean isRefreshTokenValid(String userName, String token) {
        String key = REFRESH_TOKEN_PREFIX + userName;
        String storedToken = redisTemplate.opsForValue().get(key);
        return token.equals(storedToken);
    }

    public void removeAccessToken(String username) {
        redisTemplate.delete(ACCESS_TOKEN_PREFIX + username);
    }

    public void removeRefreshToken(String username) {
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + username);
    }
}

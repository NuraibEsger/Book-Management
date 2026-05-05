package az.idtech.bookmanagement.services;

import az.idtech.bookmanagement.dao.entities.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {
    @Value("${jwt.secret-key}")
    private String secret;

    @Getter
    @Value("${jwt.access-token-expiration}")
    private long accessTokenValiditySeconds;

    @Getter
    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenValiditySeconds;

    public String generateAccessToken(UserEntity userEntity) {
        return Jwts.builder()
                .subject(userEntity.getUsername())
                .claim("roles", userEntity.getRoles())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + (accessTokenValiditySeconds * 60 * 1000)))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("type", "refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + (refreshTokenValiditySeconds * 60 * 1000)))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean isRefreshToken(String token) {
        return "refresh".equals(extractAllClaims(token).get("type", String.class));
    }

    public String extractUserName(String token) {
        return extractAllClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractAllClaims(token).get("roles", List.class);
    }

    public boolean isTokenValid(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
}

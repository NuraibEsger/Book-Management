package az.idtech.bookmanagement.configs;

import az.idtech.bookmanagement.services.JwtService;
import az.idtech.bookmanagement.services.TokenStorageService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter
{
    private final JwtService jwtService;
    private final TokenStorageService tokenStorageService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if (jwtService.isTokenValid(token)) {
            return;
        }

        String userName = jwtService.extractUserName(token);

        if (userName == null || !tokenStorageService.isAccessTokenValid(userName, token)) {
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            List<SimpleGrantedAuthority> authorities = jwtService.extractRoles(token).stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userName, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}

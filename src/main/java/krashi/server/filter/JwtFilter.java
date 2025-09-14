package krashi.server.filter;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import krashi.server.configuration.CustomUserDetailsService;
import krashi.server.exception.InvalidCredentialsException;
import krashi.server.exception.JwtTokenExpiredException;
import krashi.server.service.JwtService;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
                String authHeader = request.getHeader("Authorization");
                String jwtToken = null;
                String username = null;

                try {
                    if(authHeader != null && authHeader.startsWith("Bearer ")) {
                        jwtToken = authHeader.substring(7);
                    } else {
                        jwtToken = getJwtFromCookie(request);
                    }

                    if(jwtToken != null) {
                        username = jwtService.extractUsername(jwtToken);
                    }
                        

                    if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        
                        if(jwtService.isTokenValid(jwtToken, userDetails)) {
                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                            );
                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        }
                    }
                } catch (JwtTokenExpiredException | InvalidCredentialsException | io.jsonwebtoken.ExpiredJwtException e) {}
                
                filterChain.doFilter(request, response);
    }

    private String getJwtFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("jwt-cookie")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
    
}

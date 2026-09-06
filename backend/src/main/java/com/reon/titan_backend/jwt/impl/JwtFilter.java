package com.reon.titan_backend.jwt.impl;

import com.reon.titan_backend.jwt.JwtService;
import com.reon.titan_backend.service.security.CustomUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Runs once per request, before Spring Security checks the rules in SecurityConfig.
 * Its only job: if the request carries a valid jwt, put the user into the SecurityContext.
 * It never rejects a request itself - that is the job of the authorization rules.
 */
@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailService userDetailService;

    public JwtFilter(JwtService jwtService, CustomUserDetailService userDetailService) {
        this.jwtService = jwtService;
        this.userDetailService = userDetailService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    )
            throws ServletException, IOException {

        // 1. no token -> nothing to authenticate. permitAll endpoints still work,
        //    protected ones will be rejected later with 401.
        String jwt = jwtService.extractJwtFromRequest(request);
        if (!StringUtils.hasText(jwt)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. someone already authenticated this request, don't redo the db lookup
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (jwtService.isTokenValid(jwt)) {
                String username = jwtService.extractUsernameFromToken(jwt);

                // 3. load the user fresh from the db so a disabled/deleted account
                //    cannot keep using an old but still unexpired token
                UserDetails userDetails = userDetailService.loadUserByUsername(username);

                if (userDetails.isEnabled()) {
                    // 4. credentials are null: the signed token already proved who they are,
                    //    we never see the password here
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 5. this is what makes @PreAuthorize / .authenticated() / .hasRole() pass
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Authenticated {} from jwt", username);
                }
            }
        } catch (UsernameNotFoundException exception) {
            log.warn("Token points to an account that no longer exists");
        } catch (Exception exception) {
            // never let a bad token blow up the request - just stay unauthenticated
            log.warn("Could not authenticate from jwt: {}", exception.getMessage());
        }

        // 6. ALWAYS continue the chain, whatever happened above
        filterChain.doFilter(request, response);
    }
}

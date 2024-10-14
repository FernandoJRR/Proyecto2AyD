package com.university.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.university.services.authentication.JwtGeneratorService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtGeneratorService jwtGeneratorService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String header = request.getHeader("Authorization");
        System.out.println("header header");
        System.out.println(header);

        if (header != null && header.startsWith("Bearer ")) {
            String jwt = header.substring(7);
            String user = jwtGeneratorService.extractUserName(jwt);
            System.out.println(jwt);
            System.out.println(user);

            if (user != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(user);
                System.out.println("funciona");
                System.out.println(userDetails.getUsername());

                if (jwtGeneratorService.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    System.out.println("User authenticated: " + user); // Debugging
                    // Imprimir roles del usuario
                    Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
                    System.out.println("User roles: " + authorities.stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.joining(", ")));
                } else {
                    System.out.println("JWT validation failed."); // Debugging
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}

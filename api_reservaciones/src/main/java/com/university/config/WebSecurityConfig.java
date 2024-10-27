package com.university.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.university.services.authentication.AuthenticationService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{

    @Autowired
    private AuthenticationService service;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    /**
     * Configuracion de los roles autorizados para hacer request con urls
     * especiales
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                .antMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .antMatchers("/api/**/public/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/**/usuario/**").hasAnyRole("ADMINISTRADOR", "USUARIO_CREAR")
                .antMatchers(HttpMethod.PATCH, "/api/**/usuario/**").hasAnyRole("ADMINISTRADOR", "USUARIO_MODIFICAR")
                .antMatchers(HttpMethod.POST, "/api/**/rol/**").hasAnyRole("ADMINISTRADOR", "ROL_CREAR")
                .antMatchers(HttpMethod.PATCH, "/api/**/rol/**").hasAnyRole("ADMINISTRADOR", "ROL_MODIFICAR")
                .antMatchers(HttpMethod.POST, "/api/**/negocio/**").hasAnyRole("ADMINISTRADOR", "NEGOCIO_CREAR")
                .antMatchers(HttpMethod.PATCH, "/api/**/negocio/**").hasAnyRole("ADMINISTRADOR", "NEGOCIO_MODIFICAR")
                .antMatchers(HttpMethod.POST, "/api/**/recurso/**").hasAnyRole("ADMINISTRADOR", "RECURSO_CREAR")
                .antMatchers(HttpMethod.PATCH, "/api/**/recurso/**").hasAnyRole("ADMINISTRADOR", "RECURSO_MODIFICAR")
                .antMatchers(HttpMethod.POST, "/api/**/unidad-recurso/**").hasAnyRole("ADMINISTRADOR", "UNIDAD_RECURSO_CREAR")
                .antMatchers(HttpMethod.PATCH, "/api/**/unidad-recurso/**").hasAnyRole("ADMINISTRADOR", "UNIDAD_RECURSO_MODIFICAR")
                .antMatchers(HttpMethod.POST, "/api/**/servicio/**").hasAnyRole("ADMINISTRADOR", "SERVICIO_CREAR")
                .antMatchers(HttpMethod.PATCH, "/api/**/servicio/**").hasAnyRole("ADMINISTRADOR", "SERVICIO_MODIFICAR")
                .antMatchers(HttpMethod.POST, "/api/**/reservacion/**").hasAnyRole("ADMINISTRADOR", "RESERVACION_CREAR")
                .antMatchers(HttpMethod.PATCH, "/api/**/reservacion/**").hasAnyRole("ADMINISTRADOR", "RESERVACION_MODIFICAR")
                //.antMatchers("/api/**/cliente/**").hasRole("USUARIO")
                //.antMatchers("/api/**/private/all/**").hasAnyRole("ADMIN", "USUARIO")
                //.antMatchers("/api/**/private/**").hasAnyRole("ADMIN")
                //.antMatchers("/api/**/protected/**").hasAnyRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .cors()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //agregamos el filtro jwt antes de los demas filtros
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(service);//indicamos que el manager se apoye en un servicio de autenticacion
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

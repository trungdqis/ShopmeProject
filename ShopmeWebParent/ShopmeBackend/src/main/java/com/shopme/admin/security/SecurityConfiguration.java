package com.shopme.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    public UserDetailsService userDetailsService() {
        return new ShopmeUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return authenticationProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/users/**").hasAuthority("Admin")
                .antMatchers("/categories/**", "/brands/**").hasAnyAuthority("Admin", "Editor")
                .antMatchers("/products/**").hasAnyAuthority("Admin", "Salesperson", "Editor", "Shipper")
                .antMatchers("/questions/**").hasAnyAuthority("Admin", "Assistant")
                .antMatchers("/reviews/**").hasAnyAuthority("Admin", "Assistant")
                .antMatchers("/customers/**").hasAnyAuthority("Admin", "Salesperson")
                .antMatchers("/shipping/**").hasAnyAuthority("Admin", "Salesperson")
                .antMatchers("/orders/**").hasAnyAuthority("Admin", "Salesperson", "Shipper")
                .antMatchers("/report/**").hasAnyAuthority("Admin", "Salesperson")
                .antMatchers("/articles/**").hasAnyAuthority("Admin", "Editor")
                .antMatchers("/menus/**").hasAnyAuthority("Admin", "Editor")
                .antMatchers("/settings/**").hasAuthority("Admin")
                .anyRequest().authenticated()
                .and().formLogin()
                    .loginPage("/login")
                    .usernameParameter("email")
                    .permitAll()
                .and().logout().permitAll()
                .and().rememberMe().key("AbcDefHijKlmnOpqrs_1234567890")
                    .tokenValiditySeconds(7 * 24 * 60 * 60);

        http.authenticationProvider(authenticationProvider());

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
    }
}

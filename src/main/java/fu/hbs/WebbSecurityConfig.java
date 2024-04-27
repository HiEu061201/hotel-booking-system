/*
 * Copyright (C) 2023, FPT University
 * SEP490 - SEP490_G77
 * HBS
 * Hotel Booking System
 *
 * Record of change:
 * DATE          Version    Author           DESCRIPTION
 * 04/10/2023    1.0        HieuLBM          First Deploy
 *  *
 */

package fu.hbs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


import fu.hbs.service.impl.CustomizeUserDetailsService;

@Configuration
@EnableWebSecurity

public class WebbSecurityConfig {
    private CustomizeUserDetailsService customizeUserDetailsService;

    public WebbSecurityConfig(CustomizeUserDetailsService customizeUserDetailsService) {
        super();
        this.customizeUserDetailsService = customizeUserDetailsService;
    }

    // mã hoá
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public void configureGlobal(AuthenticationManagerBuilder managerBuilder) throws Exception {
        System.out.println("Authentication manager!");
        managerBuilder.userDetailsService(customizeUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Disable csrf
        http.csrf(csrf -> csrf.disable());

        // Authentication

        // Cấu hình cho Login Form.
        http.formLogin(auth -> auth.loginPage("/login").usernameParameter("email").loginProcessingUrl("/loginProcess")
                .defaultSuccessUrl("/homepage").failureHandler((request, response, exception) -> {
                    String errorMessage;
                    if (exception instanceof UsernameNotFoundException || exception instanceof BadCredentialsException) {
                        errorMessage = "Tài khoản hoặc mật khẩu không đúng";
                    } else if (exception instanceof LockedException) {
                        errorMessage = "Tài khoản đã bị khóa!";
                    } else {
                        errorMessage = exception.getMessage();
                    }

                    request.getSession().setAttribute("errorMessage", errorMessage);
                    response.sendRedirect("/login?error");
                })
        );


        http.logout(auth -> auth.logoutUrl("/logout").logoutSuccessUrl("/login?logout")
                .deleteCookies("JSESSIONID", "remember-me").permitAll());

        http.rememberMe(auth -> auth.key("$2a$12$AcViNz3G9VDHfEpIudFr/.kRoiR.blVJXzGzlcgQwp608WnQyuA7C")
                .tokenValiditySeconds(604800).userDetailsService(customizeUserDetailsService));

        // Authorization

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/assets/**", "/", "/feedback/homepage", "/feedback/save", "/homepage", "/login", "/registration", "/services", "/service-details", "/news", "/news-details", "/hbs/**", "room/**",
                        "/submitOrder", "/vnpay-payment", "/receptionist-payment", "/error", "/api/**",
                        "/api/v1/auth/*** ",
                        "/v3/api-docs/**",
                        "/v3/api-docs.yaml",
                        "/swagger-ui/**",
                        "/swagger-ui.html"


                ).permitAll()
                .requestMatchers("/updateprofile", "/viewProfile", "/", "/changepass").hasAnyAuthority("CUSTOMER", "ADMIN", "MANAGEMENT", "RECEPTIONISTS", "ACCOUNTING", "SALEMANAGER")


                .requestMatchers("/admin/**").hasAuthority("ADMIN").requestMatchers("/customer/**")
                .hasAuthority("CUSTOMER").requestMatchers("/management/**").hasAuthority("MANAGEMENT")
                .requestMatchers("/receptionist/**").hasAuthority("RECEPTIONISTS").requestMatchers("/saleManager/**")
                .hasAuthority("SALEMANAGER").requestMatchers("/accounting/**").hasAuthority("ACCOUNTING").anyRequest()
                .authenticated());


        return http.build();
    }
}

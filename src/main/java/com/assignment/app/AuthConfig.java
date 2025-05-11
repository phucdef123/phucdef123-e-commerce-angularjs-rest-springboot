package com.assignment.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import javax.sql.DataSource;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class AuthConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(AbstractHttpConfigurer::disable);

//        http.cors(cors -> cors.configurationSource(request -> {
//            CorsConfiguration config = new CorsConfiguration();
//            config.setAllowedOrigins(List.of("http://localhost:63342")); // Cho phép frontend
//            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Các phương thức được phép
//            config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
//            config.setAllowCredentials(true);
//            return config;
//        }));

        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/cart/order/**", "/auth/profile").authenticated();
            auth.requestMatchers("/admin/**", "/admin").hasAnyRole("STAFF", "DIRECTOR");
            auth.anyRequest().permitAll();
        });

        http.exceptionHandling(denied -> {
            denied.accessDeniedPage("/auth/access/denied");
        });

        http.formLogin(login -> {
            login.loginPage("/auth/login/form");
            login.loginProcessingUrl("/auth/login/check");
            login.defaultSuccessUrl("/auth/login/success", false);
            login.failureUrl("/auth/login/error");
            login.permitAll();
        });

        http.rememberMe(rememberMe -> {
            rememberMe.rememberMeParameter("remember-me");
            rememberMe.tokenValiditySeconds(7 * 24 * 60 * 60); //7 ngayf
            rememberMe.key("sau-ma-biet-duoc");
        });

        http.logout(logout -> {
            logout.logoutUrl("/auth/logout");
            logout.logoutSuccessUrl("/auth/logout/success");
        });

        http.oauth2Login(login -> {
           login.loginPage("/auth/login/form");
           login.defaultSuccessUrl("/oauth2/login/success", false);
           login.failureUrl("/auth/login/error");
        });

        http.sessionManagement(session -> {
            session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
        });
        http.httpBasic(withDefaults());
        return http.build();
    }
}

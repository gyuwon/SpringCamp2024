package wiredcommerce.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    public Pbkdf2PasswordEncoder passwordEncoder(
        @Value("${security.password.encoder.secret}") String passwordEncoderSecret
    ) {
        return new Pbkdf2PasswordEncoder(
            passwordEncoderSecret,
            128,
            100,
            Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256
        );
    }

    @Bean
    public DefaultSecurityFilterChain securityFilterChain(
        HttpSecurity http,
        JwtProvider jwtProvider
    ) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtProvider))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/consumer/signup").permitAll()
                .requestMatchers("/api/consumer/issue-token").permitAll()
                .anyRequest().authenticated())
            .build();
    }
}

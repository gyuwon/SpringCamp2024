package wiredcommerce.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.DefaultSecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    public JwtProvider jwtProvider(@Value("${security.jwt.secret}") String jwtSecret) {
        return JwtProvider.create(jwtSecret);
    }

    @Bean
    public DefaultSecurityFilterChain securityFilterChain(
        HttpSecurity http
    ) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/consumer/signup").permitAll()
                .requestMatchers("/api/consumer/issue-token").permitAll())
            .build();
    }
}

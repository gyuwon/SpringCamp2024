package wiredcommerce.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.DefaultSecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    public AuthenticationProvider authenticationProvider(JwtDecoder jwtDecoder) {
        var authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("");
        authoritiesConverter.setAuthoritiesClaimName("roles");
        authoritiesConverter.setAuthoritiesClaimDelimiter(",");

        var authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

        var authenticationProvider = new JwtAuthenticationProvider(jwtDecoder);
        authenticationProvider.setJwtAuthenticationConverter(authenticationConverter);

        return authenticationProvider;
    }

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
        JwtProvider jwtProvider,
        AuthenticationProvider authenticationProvider
    ) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtProvider))
            .authenticationProvider(authenticationProvider)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/consumer/signup").permitAll()
                .requestMatchers("/api/consumer/issue-token").permitAll()
                .requestMatchers("/api/consumer/**").hasRole("CONSUMER")
                .requestMatchers("/api/seller/signup").permitAll()
                .requestMatchers("/api/seller/issue-token").permitAll()
                .requestMatchers("/api/seller/**").hasRole("SELLER")
                .anyRequest().authenticated())
            .build();
    }
}

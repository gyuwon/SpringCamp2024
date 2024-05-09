package wiredcommerce.security;

import java.util.stream.Collectors;
import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import static java.util.Arrays.stream;

@Component
public final class JwtProvider implements
    JwtComposer,
    JwtDecoder,
    Customizer<OAuth2ResourceServerConfigurer<HttpSecurity>.JwtConfigurer> {

    private final SecretKeySpec key;
    private final NimbusJwtDecoder decoder;

    @Autowired
    public JwtProvider(@Value("${security.jwt.secret}") String jwtSecret) {
        this(new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256"));
    }

    private JwtProvider(SecretKeySpec key) {
        this.key = key;
        this.decoder = NimbusJwtDecoder.withSecretKey(key).build();
    }

    @Override
    public String compose(String subject, String[] roles) {
        return Jwts
            .builder()
            .signWith(key)
            .setSubject(subject)
            .claim("roles", stream(roles)
                .map(role -> "ROLE_" + role)
                .collect(Collectors.joining(",")))
            .compact();
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        return decoder.decode(token);
    }

    @Override
    public void customize(OAuth2ResourceServerConfigurer.JwtConfigurer configurer) {
        configurer.decoder(this);
    }
}

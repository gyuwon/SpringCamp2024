package wiredcommerce.security;

import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

@Component
public final class JwtProvider implements
    JwtComposer,
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
    public String compose(String subject) {
        return Jwts.builder().signWith(key).setSubject(subject).compact();
    }

    @Override
    public void customize(OAuth2ResourceServerConfigurer.JwtConfigurer configurer) {
        configurer.decoder(decoder);
    }
}

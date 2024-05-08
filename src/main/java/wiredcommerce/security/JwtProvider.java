package wiredcommerce.security;

import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.Jwts;

public final class JwtProvider implements JwtComposer {

    private final SecretKeySpec key;

    public JwtProvider(SecretKeySpec key) {
        this.key = key;
    }

    public static JwtProvider create(String secret) {
        var key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        return new JwtProvider(key);
    }

    @Override
    public String compose(String subject) {
        return Jwts.builder().signWith(key).setSubject(subject).compact();
    }
}

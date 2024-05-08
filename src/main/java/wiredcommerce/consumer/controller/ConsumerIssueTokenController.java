package wiredcommerce.consumer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wiredcommerce.consumer.query.IssueToken;
import wiredcommerce.consumer.result.TokenCarrier;
import wiredcommerce.data.ConsumerJpaRepository;
import wiredcommerce.security.JwtComposer;

@RestController
public record ConsumerIssueTokenController(
    ConsumerJpaRepository repository,
    PasswordEncoder passwordEncoder,
    JwtComposer jwtComposer) {

    @PostMapping("/api/consumer/issue-token")
    public ResponseEntity<TokenCarrier> issueToken(@RequestBody IssueToken query) {
        return repository
            .findByEmail(query.email())
            .filter(consumer -> passwordEncoder.matches(
                query.password(),
                consumer.getEncodedPassword()
            ))
            .map(consumer -> jwtComposer.compose("subject"))
            .map(TokenCarrier::new)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.badRequest().build());
    }
}

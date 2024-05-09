package wiredcommerce.seller.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wiredcommerce.data.SellerJpaRepository;
import wiredcommerce.query.IssueToken;
import wiredcommerce.result.TokenCarrier;
import wiredcommerce.security.JwtComposer;

@RestController
public record SellerIssueTokenController(
    SellerJpaRepository repository,
    PasswordEncoder passwordEncoder,
    JwtComposer jwtComposer
) {

    @PostMapping("/api/seller/issue-token")
    public ResponseEntity<TokenCarrier> issueToken(@RequestBody IssueToken query) {
        return repository
            .findByEmail(query.email())
            .filter(seller -> passwordEncoder.matches(
                query.password(),
                seller.getEncodedPassword()
            ))
            .map(seller -> jwtComposer.compose(
                seller.getId().toString(),
                new String[] { "SELLER" }
            ))
            .map(TokenCarrier::new)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.badRequest().build());
    }
}

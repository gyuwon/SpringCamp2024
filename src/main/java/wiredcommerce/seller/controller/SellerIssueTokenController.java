package wiredcommerce.seller.controller;

import org.springframework.http.ResponseEntity;
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
    JwtComposer jwtComposer
) {

    @PostMapping("/api/seller/issue-token")
    public ResponseEntity<TokenCarrier> issueToken(@RequestBody IssueToken query) {
        return repository
            .findByEmail(query.email())
            .filter(seller -> seller.getEncodedPassword().equals(query.password()))
            .map(seller -> jwtComposer.compose("subject"))
            .map(TokenCarrier::new)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.badRequest().build());
    }
}

package wiredcommerce.consumer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wiredcommerce.consumer.query.IssueToken;
import wiredcommerce.consumer.result.TokenCarrier;
import wiredcommerce.data.ConsumerJpaRepository;

@RestController
public record ConsumerIssueTokenController(ConsumerJpaRepository repository) {

    @PostMapping("/api/consumer/issue-token")
    public ResponseEntity<TokenCarrier> issueToken(@RequestBody IssueToken query) {
        return repository
            .findByEmail(query.email())
            .filter(consumer -> consumer.getPassword().equals(query.password()))
            .map(consumer -> ResponseEntity.ok((TokenCarrier) null))
            .orElse(ResponseEntity.badRequest().build());
    }
}

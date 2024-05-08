package wiredcommerce.consumer.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import wiredcommerce.consumer.view.ConsumerView;
import wiredcommerce.data.ConsumerJpaRepository;

@RestController
public record ConsumerMeController(ConsumerJpaRepository repository) {

    @GetMapping("/api/consumer/me")
    public ResponseEntity<ConsumerView> me(Principal principal) {
        return repository
            .findById(Long.parseLong(principal.getName()))
            .map(consumer -> new ConsumerView(consumer.getId(), consumer.getEmail()))
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(401).build());
    }
}

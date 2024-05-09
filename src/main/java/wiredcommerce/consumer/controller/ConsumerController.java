package wiredcommerce.consumer.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wiredcommerce.consumer.command.ChangePhoneNumber;
import wiredcommerce.consumer.view.ConsumerView;
import wiredcommerce.data.ConsumerEntity;
import wiredcommerce.data.ConsumerJpaRepository;
import wiredcommerce.model.Patterns;

@RestController
public record ConsumerController(ConsumerJpaRepository repository) {

    @GetMapping("/api/consumer/me")
    public ResponseEntity<ConsumerView> me(Principal principal) {
        ConsumerEntity entity = repository.get(principal);
        ConsumerView view = new ConsumerView(
            entity.getId(),
            entity.getEmail(),
            entity.getPhoneNumber()
        );
        return ResponseEntity.ok(view);
    }

    @PostMapping("/api/consumer/change-phone-number")
    public ResponseEntity<Void> changePhoneNumber(
        Principal principal,
        @RequestBody ChangePhoneNumber command
    ) {
        if (command.phoneNumber().matches(Patterns.PHONE_NUMBER)) {
            ConsumerEntity consumer = repository.get(principal);
            consumer.setPhoneNumber(command.phoneNumber());
            repository.save(consumer);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}

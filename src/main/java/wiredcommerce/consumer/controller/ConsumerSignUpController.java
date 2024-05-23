package wiredcommerce.consumer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wiredcommerce.consumer.command.SignUp;
import wiredcommerce.data.ConsumerEntity;
import wiredcommerce.data.ConsumerJpaRepository;

@RestController
public record ConsumerSignUpController(ConsumerJpaRepository repository) {

    @PostMapping("/api/consumer/signup")
    public ResponseEntity<Void> signUp(@RequestBody SignUp command) {
        ConsumerEntity consumer = ConsumerEntity.builder().email(command.email()).build();
        try {
            repository.save(consumer);
        } catch (Exception exception) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.noContent().build();
    }
}

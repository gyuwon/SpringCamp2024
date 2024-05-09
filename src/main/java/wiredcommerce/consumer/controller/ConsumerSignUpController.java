package wiredcommerce.consumer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wiredcommerce.consumer.command.SignUp;
import wiredcommerce.data.ConsumerEntity;
import wiredcommerce.data.ConsumerJpaRepository;
import wiredcommerce.model.Patterns;

@RestController
public record ConsumerSignUpController(
    ConsumerJpaRepository repository,
    PasswordEncoder passwordEncoder
) {

    @PostMapping("/api/consumer/signup")
    public ResponseEntity<Void> signUp(@RequestBody SignUp command) {
        String email = command.email();

        if (email.matches(Patterns.EMAIL) == false) {
            return ResponseEntity.badRequest().build();
        }

        ConsumerEntity consumer = ConsumerEntity
            .builder()
            .email(email)
            .encodedPassword(passwordEncoder.encode(command.password()))
            .build();

        try {
            repository.save(consumer);
        } catch (Exception exception) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.noContent().build();
    }
}

package wiredcommerce.seller.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wiredcommerce.data.SellerEntity;
import wiredcommerce.data.SellerJpaRepository;
import wiredcommerce.seller.command.SignUp;

@RestController
public record SellerSignUpController(
    SellerJpaRepository repository,
    PasswordEncoder passwordEncoder
) {

    @PostMapping("/api/seller/signup")
    public ResponseEntity<Void> signUp(@RequestBody SignUp command) {
        SellerEntity seller = SellerEntity
            .builder()
            .email(command.email())
            .encodedPassword(passwordEncoder.encode(command.password()))
            .phoneNumber(command.phoneNumber())
            .build();
        try {
            repository.save(seller);
        } catch (Exception exception) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }
}
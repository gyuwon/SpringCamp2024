package wiredcommerce.seller.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import wiredcommerce.data.SellerEntity;
import wiredcommerce.data.SellerJpaRepository;
import wiredcommerce.seller.view.SellerView;

@RestController
public record SellerController(SellerJpaRepository repository) {

    @GetMapping("/api/seller/me")
    public ResponseEntity<SellerView> me(Principal principal) {
        SellerEntity seller = repository.get(principal);
        SellerView view = new SellerView(
            seller.getId(),
            seller.getEmail(),
            seller.getPhoneNumber()
        );
        return ResponseEntity.ok(view);
    }
}

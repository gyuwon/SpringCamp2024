package wiredcommerce.seller.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import wiredcommerce.data.SellerEntity;
import wiredcommerce.data.SellerJpaRepository;
import wiredcommerce.seller.view.SellerSelfView;

@RestController
public record SellerController(SellerJpaRepository repository) {

    @GetMapping("/api/seller/me")
    public ResponseEntity<SellerSelfView> me(Principal principal) {
        SellerEntity seller = repository.get(principal);
        SellerSelfView view = new SellerSelfView(
            seller.getId(),
            seller.getEmail(),
            seller.getUsername(),
            seller.getPhoneNumber()
        );
        return ResponseEntity.ok(view);
    }
}

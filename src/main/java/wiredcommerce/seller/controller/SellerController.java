package wiredcommerce.seller.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import wiredcommerce.data.SellerEntity;
import wiredcommerce.data.SellerJpaRepository;
import wiredcommerce.security.User;
import wiredcommerce.seller.view.SellerSelfView;

@RestController
public record SellerController(SellerJpaRepository repository) {

    @GetMapping("/api/seller/me")
    public ResponseEntity<SellerSelfView> me(@User long sellerId) {
        SellerEntity seller = repository.findById(sellerId).orElseThrow();
        SellerSelfView view = new SellerSelfView(
            seller.getId(),
            seller.getEmail(),
            seller.getUsername(),
            seller.getPhoneNumber()
        );
        return ResponseEntity.ok(view);
    }
}

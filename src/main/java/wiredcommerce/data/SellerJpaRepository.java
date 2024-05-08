package wiredcommerce.data;

import java.security.Principal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerJpaRepository extends JpaRepository<SellerEntity, Long> {

    Optional<SellerEntity> findByEmail(String email);

    default SellerEntity get(Principal principal) {
        return findById(Long.parseLong(principal.getName())).orElseThrow();
    }
}

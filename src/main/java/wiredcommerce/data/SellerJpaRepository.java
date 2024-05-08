package wiredcommerce.data;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerJpaRepository extends JpaRepository<SellerEntity, Long> {

    Optional<SellerEntity> findByEmail(String email);
}

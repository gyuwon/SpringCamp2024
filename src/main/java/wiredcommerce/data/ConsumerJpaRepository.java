package wiredcommerce.data;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsumerJpaRepository extends JpaRepository<ConsumerEntity, Long> {

    Optional<ConsumerEntity> findByEmail(String email);
}

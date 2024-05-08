package wiredcommerce.data;

import java.security.Principal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsumerJpaRepository extends JpaRepository<ConsumerEntity, Long> {

    default ConsumerEntity get(Principal principal) {
        long id = Long.parseLong(principal.getName());
        return findById(id).orElseThrow(InvalidPrincipalException::new);
    }

    Optional<ConsumerEntity> findByEmail(String email);
}

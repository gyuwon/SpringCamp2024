package wiredcommerce.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(indexes = {
    @Index(columnList = "email", unique = true),
    @Index(columnList = "username", unique = true)
})
public class SellerEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Setter
    private String email;

    @Setter
    private String username;

    @Setter
    @Column(length = 1000)
    private String encodedPassword;

    @Setter
    private String phoneNumber;
}

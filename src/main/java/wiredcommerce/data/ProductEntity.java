package wiredcommerce.data;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(indexes = {
    @Index(columnList = "productId", unique = true),
    @Index(columnList = "sellerId")
})
public class ProductEntity {

    @Id
    @GeneratedValue
    private Long id;

    private UUID productId;

    private long sellerId;

    private String name;

    private String description;

    private int price;

    private int stockQuantity;
}

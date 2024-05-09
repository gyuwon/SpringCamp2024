package wiredcommerce.commandmodel;

import java.util.UUID;

public record Product(
    UUID id,
    long sellerId,
    String name,
    String description,
    int price,
    int stockQuantity
) {
}

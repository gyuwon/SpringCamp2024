package wiredcommerce.view;

import java.util.UUID;

public record ProductView(
    UUID id,
    SellerView seller,
    String name,
    String description,
    double price,
    int stockQuantity
) { }

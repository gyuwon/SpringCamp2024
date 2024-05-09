package wiredcommerce.command;

public record AddProduct(
    long sellerId,
    String name,
    String description,
    int price,
    int stockQuantity
) {
}

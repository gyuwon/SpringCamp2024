package wiredcommerce.seller.command;

public record AddNewProduct(
    String name,
    String description,
    int price,
    int stockQuantity
) { }

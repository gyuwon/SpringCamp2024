package wiredcommerce.query;

public record GetProducts(
    Integer minPriceInclusive,
    Integer maxPriceExclusive,
    String continuationToken
) { }

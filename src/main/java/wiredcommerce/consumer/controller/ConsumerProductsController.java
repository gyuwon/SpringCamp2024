package wiredcommerce.consumer.controller;

import jakarta.persistence.EntityManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wiredcommerce.query.GetProducts;
import wiredcommerce.querymodel.GetProductsQueryProcessor;
import wiredcommerce.view.Page;
import wiredcommerce.view.ProductView;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public record ConsumerProductsController(EntityManager entityManager) {

    @GetMapping("/api/consumer/products")
    public ResponseEntity<Page<ProductView>> getProducts(
        @RequestParam(required = false, name = "min-price-inclusive") Integer minPriceInclusive,
        @RequestParam(required = false, name = "max-price-exclusive") Integer maxPriceExclusive,
        @RequestParam(required = false, name = "continuation-token") String continuationToken
    ) {
        var query = new GetProducts(minPriceInclusive, maxPriceExclusive, continuationToken);
        var processor = new GetProductsQueryProcessor(entityManager);
        return ok(processor.process(query));
    }
}

package wiredcommerce.seller.controller;

import java.util.UUID;

import jakarta.persistence.EntityManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import wiredcommerce.command.AddProduct;
import wiredcommerce.commandmodel.AddProductCommandExecutor;
import wiredcommerce.commandmodel.ProductRepository;
import wiredcommerce.query.GetProductsOfSeller;
import wiredcommerce.querymodel.GetProductsOfSellerQueryProcessor;
import wiredcommerce.security.User;
import wiredcommerce.seller.command.AddNewProduct;
import wiredcommerce.view.ArrayCarrier;
import wiredcommerce.view.ProductView;

@RestController
public record SellerProductsController(
    ProductRepository repository,
    EntityManager entityManager
) {

    @PostMapping("/api/seller/products")
    public void addProduct(
        @RequestBody AddNewProduct content,
        @User long sellerId
    ) {
        var executor = new AddProductCommandExecutor(repository);
        var command = new AddProduct(
            sellerId,
            content.name(),
            content.description(),
            content.price(),
            content.stockQuantity()
        );
        executor.execute(UUID.randomUUID(), command);
    }

    @GetMapping("/api/seller/products")
    public ArrayCarrier<ProductView> getProducts(@User long sellerId) {
        var processor = new GetProductsOfSellerQueryProcessor(entityManager);
        var query = new GetProductsOfSeller(sellerId);
        return new ArrayCarrier<>(processor.process(query));
    }
}

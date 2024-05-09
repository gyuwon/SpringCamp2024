package wiredcommerce.data;

import org.springframework.data.jpa.repository.JpaRepository;
import wiredcommerce.commandmodel.Product;
import wiredcommerce.commandmodel.ProductRepository;

public interface ProductJpaRepository extends
    ProductRepository,
    JpaRepository<ProductEntity, Long> {

    default void addProduct(Product product) {
        var entity = new ProductEntity();
        entity.setProductId(product.id());
        entity.setSellerId(product.sellerId());
        entity.setName(product.name());
        entity.setDescription(product.description());
        entity.setPrice(product.price());
        entity.setStockQuantity(product.stockQuantity());
        save(entity);
    }
}

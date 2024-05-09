package test.wiredcommerce.data;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import test.wiredcommerce.AutoDomainSource;
import wiredcommerce.CommerceApplication;
import wiredcommerce.commandmodel.Product;
import wiredcommerce.data.ProductEntity;
import wiredcommerce.data.ProductJpaRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = CommerceApplication.class)
public class ProductJpaRepositoryTest {

    @ParameterizedTest
    @AutoDomainSource
    void addProduct_메서드는_상품_엔터티를_추가한다(
        Product product,
        @Autowired ProductJpaRepository sut
    ) {
        // Act
        sut.addProduct(product);

        // Assert
        List<ProductEntity> entities = sut.findAll();
        assertThat(entities).anyMatch(x -> x.getProductId().equals(product.id()));
    }

    @ParameterizedTest
    @AutoDomainSource
    void addProduct_메서드는_상품_엔터티_속성을_올바르게_설정한다(
        Product product,
        @Autowired ProductJpaRepository sut
    ) {
        // Act
        sut.addProduct(product);

        // Assert
        List<ProductEntity> entities = sut.findAll();
        ProductEntity actual = entities
            .stream()
            .filter(x -> x.getProductId().equals(product.id()))
            .findFirst()
            .orElseThrow();
        assertThat(actual.getSellerId()).isEqualTo(product.sellerId());
        assertThat(actual.getName()).isEqualTo(product.name());
        assertThat(actual.getDescription()).isEqualTo(product.description());
        assertThat(actual.getPrice()).isEqualTo(product.price());
        assertThat(actual.getStockQuantity()).isEqualTo(product.stockQuantity());
    }
}

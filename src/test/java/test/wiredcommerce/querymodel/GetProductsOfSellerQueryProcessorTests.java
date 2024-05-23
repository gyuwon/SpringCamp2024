package test.wiredcommerce.querymodel;

import java.util.List;

import autoparams.generator.Factory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import test.wiredcommerce.AutoDomainSource;
import wiredcommerce.CommerceApplication;
import wiredcommerce.commandmodel.Product;
import wiredcommerce.data.ProductJpaRepository;
import wiredcommerce.data.SellerEntity;
import wiredcommerce.data.SellerJpaRepository;
import wiredcommerce.query.GetProductsOfSeller;
import wiredcommerce.querymodel.GetProductsOfSellerQueryProcessor;
import wiredcommerce.view.ProductView;

import static org.assertj.core.api.Assertions.assertThat;
import static test.wiredcommerce.SellerIdFreezer.freezeSellerId;

@SpringBootTest(classes = CommerceApplication.class)
public class GetProductsOfSellerQueryProcessorTests {

    @ParameterizedTest
    @AutoDomainSource
    void 판매자가_소유한_모든_상품을_반환한다(
        SellerEntity seller,
        Factory<Product> factory,
        @Autowired SellerJpaRepository sellerRepository,
        @Autowired ProductJpaRepository productRepository,
        @Autowired EntityManager entityManager
    ) {
        // Arrange
        sellerRepository.save(seller);

        factory.applyCustomizer(freezeSellerId(seller));
        List<Product> products = factory.stream().limit(3).toList();
        products.forEach(productRepository::addProduct);

        var sut = new GetProductsOfSellerQueryProcessor(entityManager);

        // Act
        var query = new GetProductsOfSeller(seller.getId());
        List<ProductView> actual = sut.process(query);

        // Assert
        assertThat(actual)
            .hasSameSizeAs(products)
            .extracting(ProductView::id)
            .containsExactlyInAnyOrderElementsOf(products.stream().map(Product::id).toList());
    }

    @ParameterizedTest
    @AutoDomainSource
    void 상품_속성을_올바르게_설정한다(
        SellerEntity seller,
        Factory<Product> factory,
        @Autowired SellerJpaRepository sellerRepository,
        @Autowired ProductJpaRepository productRepository,
        @Autowired EntityManager entityManager
    ) {
        // Arrange
        sellerRepository.save(seller);

        factory.applyCustomizer(freezeSellerId(seller));
        Product product = factory.get();
        productRepository.addProduct(product);

        var sut = new GetProductsOfSellerQueryProcessor(entityManager);

        // Act
        var query = new GetProductsOfSeller(product.sellerId());
        List<ProductView> list = sut.process(query);

        // Assert
        ProductView actual = list.getFirst();
        assertThat(actual.name()).isEqualTo(product.name());
        assertThat(actual.description()).isEqualTo(product.description());
        assertThat(actual.price()).isEqualTo(product.price());
        assertThat(actual.stockQuantity()).isEqualTo(product.stockQuantity());
    }

    @ParameterizedTest
    @AutoDomainSource
    void 판매자_속성을_올바르게_설정한다(
        SellerEntity seller,
        Factory<Product> factory,
        @Autowired SellerJpaRepository sellerRepository,
        @Autowired ProductJpaRepository productRepository,
        @Autowired EntityManager entityManager
    ) {
        // Arrange
        sellerRepository.save(seller);

        factory.applyCustomizer(freezeSellerId(seller));
        Product product = factory.get();
        productRepository.addProduct(product);

        var sut = new GetProductsOfSellerQueryProcessor(entityManager);

        // Act
        var query = new GetProductsOfSeller(product.sellerId());
        List<ProductView> list = sut.process(query);

        // Assert
        ProductView actual = list.getFirst();
        assertThat(actual.seller().id()).isEqualTo(seller.getId());
        assertThat(actual.seller().username()).isEqualTo(seller.getUsername());
    }

    @ParameterizedTest
    @AutoDomainSource
    void 다른_판매자가_소유한_상품은_반환하지_않는다(
        SellerEntity seller,
        SellerEntity anotherSeller,
        Factory<Product> factory,
        @Autowired SellerJpaRepository sellerRepository,
        @Autowired ProductJpaRepository productRepository,
        @Autowired EntityManager entityManager
    ) {
        // Arrange
        sellerRepository.save(seller);
        factory.applyCustomizer(freezeSellerId(seller));
        productRepository.addProduct(factory.get());

        sellerRepository.save(anotherSeller);
        factory.applyCustomizer(freezeSellerId(anotherSeller));
        productRepository.addProduct(factory.get());

        var sut = new GetProductsOfSellerQueryProcessor(entityManager);

        // Act
        var query = new GetProductsOfSeller(seller.getId());
        List<ProductView> actual = sut.process(query);

        // Assert
        assertThat(actual).extracting(x -> x.seller().id()).containsExactly(seller.getId());
    }
}

package test.wiredcommerce.querymodel;

import java.util.List;
import java.util.UUID;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

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
import wiredcommerce.query.GetProducts;
import wiredcommerce.querymodel.GetProductsQueryProcessor;
import wiredcommerce.view.Page;
import wiredcommerce.view.ProductView;

import static org.assertj.core.api.Assertions.assertThat;
import static test.wiredcommerce.PriceGenerator.price;
import static test.wiredcommerce.SellerIdFreezer.freezeSellerId;

@SpringBootTest(classes = CommerceApplication.class)
public class GetProductsQueryProcessorTests {

    @ParameterizedTest
    @AutoDomainSource
    void 첫번째_페이지를_올바르게_반환한다(
        SellerEntity seller,
        Factory<Product> factory,
        @Autowired EntityManager entityManager,
        @Autowired SellerJpaRepository sellerRepository,
        @Autowired ProductJpaRepository productRepository
    ) {
        // Arrange
        sellerRepository.save(seller);
        factory.applyCustomizer(freezeSellerId(seller));

        productRepository.deleteAll();

        List<Product> residuals = factory.stream().limit(10).toList();
        residuals.forEach(productRepository::addProduct);

        List<Product> products = factory.stream().limit(10).toList();
        products.reversed().forEach(productRepository::addProduct);

        var sut = new GetProductsQueryProcessor(entityManager);

        // Act
        Page<ProductView> page = sut.process(new GetProducts(null, null, null));

        // Assert
        assertThat(page.items())
            .extracting(ProductView::id)
            .containsExactly(products.stream().map(Product::id).toArray(UUID[]::new));
    }

    @ParameterizedTest
    @AutoDomainSource
    void 두번째_페이지를_올바르게_반환한다(
        SellerEntity seller,
        Factory<Product> factory,
        @Autowired EntityManager entityManager,
        @Autowired SellerJpaRepository sellerRepository,
        @Autowired ProductJpaRepository productRepository
    ) {
        // Arrange
        sellerRepository.save(seller);
        factory.applyCustomizer(freezeSellerId(seller));

        productRepository.deleteAll();

        List<Product> rearResiduals = factory.stream().limit(10).toList();
        rearResiduals.forEach(productRepository::addProduct);

        List<Product> products = factory.stream().limit(10).toList();
        products.reversed().forEach(productRepository::addProduct);

        List<Product> frontResiduals = factory.stream().limit(10).toList();
        frontResiduals.forEach(productRepository::addProduct);

        var sut = new GetProductsQueryProcessor(entityManager);

        Page<ProductView> firstPage = sut.process(new GetProducts(null, null, null));
        String continuationToken = firstPage.continuationToken();

        // Act
        Page<ProductView> secondPage = sut.process(new GetProducts(null, null, continuationToken));

        // Assert
        assertThat(secondPage.items())
            .extracting(ProductView::id)
            .containsExactly(products.stream().limit(10).map(Product::id).toArray(UUID[]::new));
    }

    @ParameterizedTest
    @AutoDomainSource
    void 마지막_페이지의_continuationToken_속성을_설정하지_않는다(
        SellerEntity seller,
        Factory<Product> factory,
        @Autowired EntityManager entityManager,
        @Autowired SellerJpaRepository sellerRepository,
        @Autowired ProductJpaRepository productRepository
    ) {
        // Arrange
        sellerRepository.save(seller);
        factory.applyCustomizer(freezeSellerId(seller));

        productRepository.deleteAll();
        productRepository.addProduct(factory.get());

        var sut = new GetProductsQueryProcessor(entityManager);

        // Act
        Page<ProductView> page = sut.process(new GetProducts(null, null, null));

        // Assert
        assertThat(page.continuationToken()).isNull();
    }

    @ParameterizedTest
    @AutoDomainSource
    void 가격_범위에_해당하는_상품을_반환한다(
        SellerEntity seller,
        Factory<Product> factory,
        @Min(20000) @Max(30000) int min,
        @Min(30000) @Max(40000) int max,
        @Autowired EntityManager entityManager,
        @Autowired SellerJpaRepository sellerRepository,
        @Autowired ProductJpaRepository productRepository
    ) {
        // Arrange
        sellerRepository.save(seller);

        productRepository.deleteAll();

        factory.applyCustomizer(freezeSellerId(seller));
        factory.applyCustomizer(price(min - 10000, max + 20000));
        factory.stream().limit(100).forEach(productRepository::addProduct);

        var sut = new GetProductsQueryProcessor(entityManager);

        // Act
        Page<ProductView> page = sut.process(new GetProducts(min, max, null));

        // Assert
        assertThat(page.items()).allMatch(p -> p.price() >= min && p.price() < max);
    }

    @ParameterizedTest
    @AutoDomainSource
    void 최소가격과_가격이_일치하는_상품을_반환한다(
        SellerEntity seller,
        Factory<Product> factory,
        @Autowired EntityManager entityManager,
        @Autowired SellerJpaRepository sellerRepository,
        @Autowired ProductJpaRepository productRepository
    ) {
        // Arrange
        sellerRepository.save(seller);
        factory.applyCustomizer(freezeSellerId(seller));

        productRepository.deleteAll();

        Product product = factory.get();
        productRepository.addProduct(product);

        var sut = new GetProductsQueryProcessor(entityManager);

        // Act
        GetProducts query = new GetProducts(product.price(), product.price() + 1, null);
        Page<ProductView> page = sut.process(query);

        // Assert
        assertThat(page.items()).isNotEmpty();
    }

    @ParameterizedTest
    @AutoDomainSource
    void 최대가격과_가격이_일치하는_상품을_반환하지_않는다(
        SellerEntity seller,
        Factory<Product> factory,
        @Autowired EntityManager entityManager,
        @Autowired SellerJpaRepository sellerRepository,
        @Autowired ProductJpaRepository productRepository
    ) {
        // Arrange
        sellerRepository.save(seller);
        factory.applyCustomizer(freezeSellerId(seller));

        productRepository.deleteAll();

        Product product = factory.get();
        productRepository.addProduct(product);

        var sut = new GetProductsQueryProcessor(entityManager);

        // Act
        GetProducts query = new GetProducts(product.price() - 1, product.price(), null);
        Page<ProductView> page = sut.process(query);

        // Assert
        assertThat(page.items()).isEmpty();
    }
}

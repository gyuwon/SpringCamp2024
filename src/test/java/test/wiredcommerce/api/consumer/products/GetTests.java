package test.wiredcommerce.api.consumer.products;

import java.util.List;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import autoparams.generator.Factory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import test.wiredcommerce.AutoDomainSource;
import wiredcommerce.CommerceApplication;
import wiredcommerce.commandmodel.Product;
import wiredcommerce.consumer.command.SignUp;
import wiredcommerce.data.ProductJpaRepository;
import wiredcommerce.data.SellerEntity;
import wiredcommerce.data.SellerJpaRepository;
import wiredcommerce.view.Page;
import wiredcommerce.view.ProductView;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.RequestEntity.get;
import static test.wiredcommerce.PriceGenerator.price;
import static test.wiredcommerce.SellerIdFreezer.freezeSellerId;
import static test.wiredcommerce.api.ApiTestLanguage.getProducts;
import static test.wiredcommerce.api.ApiTestLanguage.issueConsumerToken;
import static test.wiredcommerce.api.ApiTestLanguage.signUp;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = CommerceApplication.class
)
@DisplayName("GET /api/consumer/products")
public class GetTests {

    @ParameterizedTest
    @AutoDomainSource
    void OK_상태코드를_반환한다(
        SellerEntity seller,
        Factory<Product> factory,
        SignUp signUp,
        @Autowired SellerJpaRepository sellerRepository,
        @Autowired ProductJpaRepository productRepository,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        sellerRepository.save(seller);
        factory.applyCustomizer(freezeSellerId(seller));

        productRepository.deleteAll();
        productRepository.addProduct(factory.get());

        signUp(client, signUp);
        String accessToken = issueConsumerToken(client, signUp.email(), signUp.password());

        // Act
        ResponseEntity<Page<ProductView>> response = client.exchange(
            get("/api/consumer/products")
                .header("Authorization", "Bearer " + accessToken)
                .build(),
            new ParameterizedTypeReference<>() { }
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @ParameterizedTest
    @AutoDomainSource
    void 상품_목록을_반환한다(
        SellerEntity seller,
        Factory<Product> factory,
        SignUp signUp,
        @Autowired SellerJpaRepository sellerRepository,
        @Autowired ProductJpaRepository productRepository,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        sellerRepository.save(seller);
        factory.applyCustomizer(freezeSellerId(seller));

        productRepository.deleteAll();
        Product product = factory.get();
        productRepository.addProduct(product);

        signUp(client, signUp);
        String accessToken = issueConsumerToken(client, signUp.email(), signUp.password());

        // Act
        Page<ProductView> page = getProducts(client, accessToken, null, null, null);

        // Assert
        assertThat(page.items())
            .extracting(ProductView::id)
            .containsExactly(product.id());
    }

    @ParameterizedTest
    @AutoDomainSource
    void continuationToken_속성을_올바르게_설정한다(
        SellerEntity seller,
        Factory<Product> factory,
        SignUp signUp,
        @Autowired SellerJpaRepository sellerRepository,
        @Autowired ProductJpaRepository productRepository,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        sellerRepository.save(seller);
        factory.applyCustomizer(freezeSellerId(seller));

        productRepository.deleteAll();
        Product product = factory.get();
        productRepository.addProduct(product);

        List<Product> residuals = factory.stream().limit(10).toList();
        residuals.forEach(productRepository::addProduct);

        signUp(client, signUp);
        String accessToken = issueConsumerToken(client, signUp.email(), signUp.password());

        Page<ProductView> firstPage = getProducts(client, accessToken, null, null, null);
        String continuationToken = firstPage.continuationToken();

        // Act
        Page<ProductView> secondPage = getProducts(client, accessToken, null, null, continuationToken);

        // Assert
        assertThat(secondPage.items())
            .hasSize(1)
            .extracting(ProductView::id)
            .containsExactlyInAnyOrder(product.id());
    }

    @ParameterizedTest
    @AutoDomainSource
    void 가격_범위를_지정할_수_있다(
        SellerEntity seller,
        Factory<Product> factory,
        @Min(20000) @Max(30000) int min,
        @Min(30000) @Max(40000) int max,
        SignUp signUp,
        @Autowired SellerJpaRepository sellerRepository,
        @Autowired ProductJpaRepository productRepository,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        sellerRepository.save(seller);
        factory.applyCustomizer(freezeSellerId(seller));

        productRepository.deleteAll();
        factory.applyCustomizer(freezeSellerId(seller));
        factory.applyCustomizer(price(min - 10000, max + 20000));
        factory.stream().limit(100).forEach(productRepository::addProduct);

        signUp(client, signUp);
        String accessToken = issueConsumerToken(client, signUp.email(), signUp.password());

        // Act
        Page<ProductView> page = getProducts(client, accessToken, min, max, null);

        // Assert
        assertThat(page.items()).allMatch(p -> p.price() >= min && p.price() < max);
    }
}

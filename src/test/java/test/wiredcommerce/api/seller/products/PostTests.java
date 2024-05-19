package test.wiredcommerce.api.seller.products;

import java.util.List;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import test.wiredcommerce.AutoDomainSource;
import wiredcommerce.CommerceApplication;
import wiredcommerce.query.GetProductsOfSeller;
import wiredcommerce.querymodel.GetProductsOfSellerQueryProcessor;
import wiredcommerce.seller.command.AddNewProduct;
import wiredcommerce.seller.command.SignUp;
import wiredcommerce.seller.view.SellerSelfView;
import wiredcommerce.view.ProductView;

import static org.assertj.core.api.Assertions.assertThat;
import static test.wiredcommerce.api.ApiTestLanguage.issueSellerToken;
import static test.wiredcommerce.api.ApiTestLanguage.meAsSeller;
import static test.wiredcommerce.api.ApiTestLanguage.signUp;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = CommerceApplication.class
)
@DisplayName("POST /api/seller/products")
public class PostTests {

    @ParameterizedTest
    @AutoDomainSource
    void 상품을_등록하면_200_상태코드를_반환한다(
        SignUp signUp,
        AddNewProduct addProduct,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        signUp(client, signUp);
        String token = issueSellerToken(client, signUp.email(), signUp.password());

        // Act
        RequestEntity<AddNewProduct> request = RequestEntity
            .post("/api/seller/products")
            .header("Authorization", "Bearer " + token)
            .body(addProduct);
        ResponseEntity<Void> response = client.exchange(request, Void.class);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @ParameterizedTest
    @AutoDomainSource
    void 상품을_등록하면_등록된_상품이_조회된다(
        SignUp signUp,
        AddNewProduct addProduct,
        @Autowired TestRestTemplate client,
        @Autowired EntityManager entityManager
    ) {
        // Arrange
        signUp(client, signUp);
        String token = issueSellerToken(client, signUp.email(), signUp.password());

        // Act
        RequestEntity<AddNewProduct> request = RequestEntity
            .post("/api/seller/products")
            .header("Authorization", "Bearer " + token)
            .body(addProduct);
        client.exchange(request, Void.class);

        // Assert
        SellerSelfView seller = meAsSeller(client, token);
        var processor = new GetProductsOfSellerQueryProcessor(entityManager);
        var query = new GetProductsOfSeller(seller.id());
        List<ProductView> actual = processor.process(query);
        assertThat(actual).singleElement().satisfies(x -> {
            assertThat(x.seller().id()).isEqualTo(seller.id());
            assertThat(x.seller().username()).isEqualTo(seller.username());
            assertThat(x.name()).isEqualTo(addProduct.name());
            assertThat(x.description()).isEqualTo(addProduct.description());
            assertThat(x.price()).isEqualTo(addProduct.price());
            assertThat(x.stockQuantity()).isEqualTo(addProduct.stockQuantity());
        });
    }
}

package test.wiredcommerce.api.seller.products;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import test.wiredcommerce.AutoDomainSource;
import wiredcommerce.CommerceApplication;
import wiredcommerce.seller.command.AddNewProduct;
import wiredcommerce.seller.command.SignUp;
import wiredcommerce.view.ArrayCarrier;
import wiredcommerce.view.ProductView;

import static java.util.stream.StreamSupport.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static test.wiredcommerce.api.ApiTestLanguage.addProduct;
import static test.wiredcommerce.api.ApiTestLanguage.issueSellerToken;
import static test.wiredcommerce.api.ApiTestLanguage.signUp;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = CommerceApplication.class)
@DisplayName("GET /api/seller/products")
public class GetTests {

    @ParameterizedTest
    @AutoDomainSource
    void 상품_목록을_조회하면_200_상태코드를_반환한다(
        SignUp signUp,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        signUp(client, signUp);
        String token = issueSellerToken(client, signUp.email(), signUp.password());

        // Act
        ResponseEntity<ArrayCarrier<ProductView>> response = client.exchange(
            RequestEntity
                .get("/api/seller/products")
                .header("Authorization", "Bearer " + token)
                .build(),
            new ParameterizedTypeReference<>() { }
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @ParameterizedTest
    @AutoDomainSource
    void 상품_목록을_조회하면_등록된_상품이_반환된다(
        SignUp signUp,
        List<AddNewProduct> commands,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        signUp(client, signUp);
        String token = issueSellerToken(client, signUp.email(), signUp.password());
        commands.forEach(command -> addProduct(client, token, command));

        // Act
        ResponseEntity<ArrayCarrier<ProductView>> response = client.exchange(
            RequestEntity
                .get("/api/seller/products")
                .header("Authorization", "Bearer " + token)
                .build(),
            new ParameterizedTypeReference<>() { }
        );

        // Assert
        ArrayCarrier<ProductView> body = response.getBody();
        assert body != null;
        Iterable<ProductView> actual = body.items();
        assertThat(actual).hasSize(commands.size());
        assertThat(commands).allMatch(c ->
            stream(actual.spliterator(), false)
                .anyMatch(v -> v.name().equals(c.name())));
    }
}

package test.wiredcommerce.api.seller.me;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import test.wiredcommerce.AutoDomainSource;
import wiredcommerce.data.SellerEntity;
import wiredcommerce.data.SellerJpaRepository;
import wiredcommerce.seller.command.SignUp;
import wiredcommerce.seller.view.SellerView;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static test.wiredcommerce.api.ApiTestLanguage.issueConsumerToken;
import static test.wiredcommerce.api.ApiTestLanguage.issueSellerToken;
import static test.wiredcommerce.api.ApiTestLanguage.signUp;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = wiredcommerce.CommerceApplication.class
)
@DisplayName("GET /api/seller/me")
public class GetTests {

    @ParameterizedTest
    @AutoDomainSource
    void 토큰을_사용해_요청하면_200_상태코드를_반환한다(
        SignUp signUp,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        signUp(client, signUp);
        String token = issueSellerToken(client, signUp.email(), signUp.password());

        // Act
        RequestEntity<Void> request = RequestEntity
            .get("/api/seller/me")
            .header("Authorization", "Bearer " + token)
            .build();
        ResponseEntity<SellerView> response = client.exchange(request, SellerView.class);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @ParameterizedTest
    @AutoDomainSource
    void 응답_컨텐트는_올바른_이메일을_포함한다(
        SignUp signUp,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        signUp(client, signUp);
        String token = issueSellerToken(client, signUp.email(), signUp.password());

        // Act
        RequestEntity<Void> request = RequestEntity
            .get("/api/seller/me")
            .header("Authorization", "Bearer " + token)
            .build();
        ResponseEntity<SellerView> response = client.exchange(request, SellerView.class);

        // Assert
        SellerView seller = response.getBody();
        assertThat(requireNonNull(seller).email()).isEqualTo(signUp.email());
    }

    @ParameterizedTest
    @AutoDomainSource
    void 응답_컨텐트는_올바른_전화번호를_포함한다(
        SignUp signUp,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        signUp(client, signUp);
        String token = issueSellerToken(client, signUp.email(), signUp.password());

        // Act
        RequestEntity<Void> request = RequestEntity
            .get("/api/seller/me")
            .header("Authorization", "Bearer " + token)
            .build();
        ResponseEntity<SellerView> response = client.exchange(request, SellerView.class);

        // Assert
        SellerView seller = response.getBody();
        assertThat(requireNonNull(seller).phoneNumber()).isEqualTo(signUp.phoneNumber());
    }

    @ParameterizedTest
    @AutoDomainSource
    void 응답_컨텐트는_올바른_식별자를_포함한다(
        SignUp signUp,
        @Autowired TestRestTemplate client,
        @Autowired SellerJpaRepository repository
    ) {
        // Arrange
        signUp(client, signUp);
        String token = issueSellerToken(client, signUp.email(), signUp.password());

        // Act
        RequestEntity<Void> request = RequestEntity
            .get("/api/seller/me")
            .header("Authorization", "Bearer " + token)
            .build();
        ResponseEntity<SellerView> response = client.exchange(request, SellerView.class);

        // Assert
        SellerView view = response.getBody();
        SellerEntity entity = repository.findByEmail(signUp.email()).orElseThrow();
        assertThat(requireNonNull(view).id()).isEqualTo(entity.getId());
    }

    @ParameterizedTest
    @AutoDomainSource
    void 구매자_토큰을_사용해_접근하면_403_상태코드를_반환한다(
        wiredcommerce.consumer.command.SignUp signUp,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        signUp(client, signUp);
        String consumerToken = issueConsumerToken(client, signUp.email(), signUp.password());

        // Act
        RequestEntity<Void> request = RequestEntity
            .get("/api/seller/me")
            .header("Authorization", "Bearer " + consumerToken)
            .build();
        ResponseEntity<SellerView> response = client.exchange(request, SellerView.class);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(403);
    }
}

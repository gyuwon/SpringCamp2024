package test.wiredcommerce.api.consumer.me;

import autoparams.AutoSource;
import autoparams.BrakeBeforeAnnotation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import wiredcommerce.consumer.command.SignUp;
import wiredcommerce.consumer.view.ConsumerView;
import wiredcommerce.data.ConsumerEntity;
import wiredcommerce.data.ConsumerJpaRepository;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static test.wiredcommerce.api.ApiTestLanguage.issueConsumerToken;
import static test.wiredcommerce.api.ApiTestLanguage.signUp;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = wiredcommerce.CommerceApplication.class
)
@DisplayName("GET /api/consumer/me")
public class GetTests {

    @ParameterizedTest
    @AutoSource
    @BrakeBeforeAnnotation(Autowired.class)
    void 토큰을_사용해_요청하면_200_상태코드를_반환한다(
        SignUp signUp,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        signUp(client, signUp);
        String token = issueConsumerToken(client, signUp.email(), signUp.password());

        // Act
        RequestEntity<Void> request = RequestEntity
            .get("/api/consumer/me")
            .header("Authorization", "Bearer " + token)
            .build();
        ResponseEntity<ConsumerView> response = client.exchange(request, ConsumerView.class);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @ParameterizedTest
    @AutoSource
    @BrakeBeforeAnnotation(Autowired.class)
    void 응답_컨텐트는_올바른_이메일을_포함한다(
        SignUp signUp,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        signUp(client, signUp);
        String token = issueConsumerToken(client, signUp.email(), signUp.password());

        // Act
        RequestEntity<Void> request = RequestEntity
            .get("/api/consumer/me")
            .header("Authorization", "Bearer " + token)
            .build();
        ResponseEntity<ConsumerView> response = client.exchange(request, ConsumerView.class);

        // Assert
        ConsumerView view = response.getBody();
        assertThat(requireNonNull(view).email()).isEqualTo(signUp.email());
    }

    @ParameterizedTest
    @AutoSource
    @BrakeBeforeAnnotation(Autowired.class)
    void 응답_컨텐트는_올바른_식별자를_포함한다(
        SignUp signUp,
        @Autowired TestRestTemplate client,
        @Autowired ConsumerJpaRepository repository
    ) {
        // Arrange
        signUp(client, signUp);
        String token = issueConsumerToken(client, signUp.email(), signUp.password());

        // Act
        RequestEntity<Void> request = RequestEntity
            .get("/api/consumer/me")
            .header("Authorization", "Bearer " + token)
            .build();
        ResponseEntity<ConsumerView> response = client.exchange(request, ConsumerView.class);

        // Assert
        ConsumerView view = response.getBody();
        ConsumerEntity entity = repository.findByEmail(signUp.email()).orElseThrow();
        assertThat(requireNonNull(view).id()).isEqualTo(entity.getId());
    }

    @ParameterizedTest
    @AutoSource
    @BrakeBeforeAnnotation(Autowired.class)
    void 구매자가_존재하지_않는_경우_401_상태코드를_반환한다(
        SignUp signUp,
        @Autowired TestRestTemplate client,
        @Autowired ConsumerJpaRepository repository
    ) {
        // Arrange
        signUp(client, signUp);
        String token = issueConsumerToken(client, signUp.email(), signUp.password());
        ConsumerEntity consumer = repository.findByEmail(signUp.email()).orElseThrow();
        repository.delete(consumer);

        // Act
        RequestEntity<Void> request = RequestEntity
            .get("/api/consumer/me")
            .header("Authorization", "Bearer " + token)
            .build();
        ResponseEntity<ConsumerView> response = client.exchange(request, ConsumerView.class);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(401);
    }
}

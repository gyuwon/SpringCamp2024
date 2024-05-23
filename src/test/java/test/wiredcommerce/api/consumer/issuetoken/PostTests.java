package test.wiredcommerce.api.consumer.issuetoken;

import autoparams.AutoSource;
import autoparams.BrakeBeforeAnnotation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import wiredcommerce.CommerceApplication;
import wiredcommerce.consumer.command.SignUp;
import wiredcommerce.consumer.query.IssueToken;
import wiredcommerce.consumer.result.TokenCarrier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = CommerceApplication.class
)
@DisplayName("POST /api/consumer/issue-token")
public class PostTests {

    @ParameterizedTest
    @AutoSource
    @BrakeBeforeAnnotation(Autowired.class)
    void 올바른_정보를_사용해_요청하면_200_상태코드를_반환한다(
        SignUp signUp,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        client.postForEntity("/api/consumer/signup", signUp, Void.class);
        String path = "/api/consumer/issue-token";

        // Act
        ResponseEntity<TokenCarrier> response = client.postForEntity(
            path,
            new IssueToken(signUp.email(), signUp.password()),
            TokenCarrier.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @ParameterizedTest
    @AutoSource
    @BrakeBeforeAnnotation(Autowired.class)
    void 존재하지_않는_이메일을_사용해_요청하면_400_상태코드를_반환한다(
        IssueToken query,
        @Autowired TestRestTemplate client
    ) {
        // Act
        ResponseEntity<TokenCarrier> response = client.postForEntity(
            "/api/consumer/issue-token",
            query,
            TokenCarrier.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @ParameterizedTest
    @AutoSource
    @BrakeBeforeAnnotation(Autowired.class)
    void 올바르지_않은_비밀번호를_사용해_요청하면_400_상태코드를_반환한다(
        SignUp signUp,
        String wrongPassword,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        client.postForEntity("/api/consumer/signup", signUp, Void.class);

        // Act
        ResponseEntity<TokenCarrier> response = client.postForEntity(
            "/api/consumer/issue-token",
            new IssueToken(signUp.email(), wrongPassword),
            TokenCarrier.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }
}

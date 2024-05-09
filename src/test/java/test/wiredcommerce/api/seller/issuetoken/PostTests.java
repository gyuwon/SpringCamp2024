package test.wiredcommerce.api.seller.issuetoken;

import java.util.Objects;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import test.wiredcommerce.AutoDomainSource;
import wiredcommerce.query.IssueToken;
import wiredcommerce.result.TokenCarrier;
import wiredcommerce.seller.command.SignUp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static test.wiredcommerce.api.ApiTestLanguage.signUp;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = wiredcommerce.CommerceApplication.class
)
@DisplayName("POST /api/seller/issue-token")
public class PostTests {

    @ParameterizedTest
    @AutoDomainSource
    void 올바른_정보를_사용해_요청하면_200_상태코드를_반환한다(
        SignUp signUp,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        signUp(client, signUp);
        String path = "/api/seller/issue-token";

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
    @AutoDomainSource
    void 존재하지_않는_이메일을_사용해_요청하면_400_상태코드를_반환한다(
        IssueToken query,
        @Autowired TestRestTemplate client
    ) {
        // Act
        ResponseEntity<TokenCarrier> response = client.postForEntity(
            "/api/seller/issue-token",
            query,
            TokenCarrier.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @ParameterizedTest
    @AutoDomainSource
    void 올바르지_않은_비밀번호를_사용해_요청하면_400_상태코드를_반환한다(
        SignUp signUp,
        String wrongPassword,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        signUp(client, signUp);

        // Act
        ResponseEntity<TokenCarrier> response = client.postForEntity(
            "/api/seller/issue-token",
            new IssueToken(signUp.email(), wrongPassword),
            TokenCarrier.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @ParameterizedTest
    @AutoDomainSource
    void 올바른_정보를_사용해_요청하면_JWT를_반환한다(
        SignUp signUp,
        @Autowired TestRestTemplate client,
        @Value("${security.jwt.secret}") String jwtSecret
    ) {
        // Arrange
        signUp(client, signUp);

        // Act
        ResponseEntity<TokenCarrier> response = client.postForEntity(
            "/api/seller/issue-token",
            new IssueToken(signUp.email(), signUp.password()),
            TokenCarrier.class
        );

        // Assert
        String token = Objects.requireNonNull(response.getBody()).token();
        assertDoesNotThrow(() -> getJwtDecoder(jwtSecret).decode(token));
    }

    private static JwtDecoder getJwtDecoder(String jwtSecret) {
        var key = new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}

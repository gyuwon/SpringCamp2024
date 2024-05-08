package test.wiredcommerce.api.seller.signup;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import test.wiredcommerce.api.AutoDomainSource;
import wiredcommerce.CommerceApplication;
import wiredcommerce.seller.command.SignUp;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = CommerceApplication.class
)
@DisplayName("POST /api/seller/signup")
public class PostTests {

    @ParameterizedTest
    @AutoDomainSource
    void 올바른_정보를_사용해_요청하면_성공_상태코드를_반환한다(
        SignUp signUp,
        @Autowired TestRestTemplate client
    ) {
        String path = "/api/seller/signup";
        ResponseEntity<Void> response = client.postForEntity(path, signUp, Void.class);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @ParameterizedTest
    @AutoDomainSource
    void 존재하는_이메일_주소를_사용해_요청하면_400_상태코드를_반환한다(
        SignUp signUp,
        String otherPassword,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        String path = "/api/seller/signup";
        client.postForEntity(path, signUp, Void.class);

        // Act
        ResponseEntity<Void> response = client.postForEntity(
            path,
            new SignUp(signUp.email(), otherPassword, signUp.phoneNumber()),
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }
}

package test.wiredcommerce.api.seller.signup;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import test.wiredcommerce.api.AutoDomainSource;
import wiredcommerce.CommerceApplication;
import wiredcommerce.data.SellerEntity;
import wiredcommerce.data.SellerJpaRepository;
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

    @ParameterizedTest
    @AutoDomainSource
    void 비밀번호를_암호화한다(
        SignUp signUp,
        @Autowired TestRestTemplate client,
        @Autowired SellerJpaRepository repository,
        @Autowired PasswordEncoder passwordEncoder
    ) {
        // Arrange
        String path = "/api/seller/signup";

        // Act
        client.postForEntity(path, signUp, Void.class);

        // Assert
        SellerEntity seller = repository.findByEmail(signUp.email()).orElseThrow();
        String encodedPassword = seller.getEncodedPassword();
        assertThat(passwordEncoder.matches(signUp.password(), encodedPassword)).isTrue();
    }
}

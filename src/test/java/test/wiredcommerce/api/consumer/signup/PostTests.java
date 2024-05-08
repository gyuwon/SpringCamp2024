package test.wiredcommerce.api.consumer.signup;

import autoparams.AutoSource;
import autoparams.BrakeBeforeAnnotation;
import autoparams.ValueAutoSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import wiredcommerce.CommerceApplication;
import wiredcommerce.consumer.command.SignUp;
import wiredcommerce.data.ConsumerEntity;
import wiredcommerce.data.ConsumerJpaRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = CommerceApplication.class
)
@DisplayName("POST /api/consumer/signup")
public record PostTests(@Autowired TestRestTemplate client) {

    @ParameterizedTest
    @AutoSource
    void 올바른_정보를_사용해_요청하면_성공_상태코드를_반환한다(SignUp signUp) {
        String path = "/api/consumer/signup";
        ResponseEntity<Void> response = client.postForEntity(path, signUp, Void.class);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @ParameterizedTest
    @AutoSource
    void 존재하는_이메일_주소를_사용해_요청하면_400_상태코드를_반환한다(
        SignUp signUp,
        String otherPassword
    ) {
        // Arrange
        String path = "/api/consumer/signup";
        client.postForEntity(path, signUp, Void.class);

        // Act
        ResponseEntity<Void> response = client.postForEntity(
            path,
            new SignUp(signUp.email(), otherPassword),
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @ParameterizedTest
    @AutoSource
    @BrakeBeforeAnnotation(Autowired.class)
    void 비밀번호를_암호화한다(
        SignUp signUp,
        @Autowired ConsumerJpaRepository repository,
        @Autowired PasswordEncoder passwordEncoder
    ) {
        // Act
        client.postForEntity("/api/consumer/signup", signUp, Void.class);

        // Assert
        ConsumerEntity consumer = repository.findByEmail(signUp.email()).orElseThrow();
        String encodedPassword = consumer.getEncodedPassword();
        assertThat(passwordEncoder.matches(signUp.password(), encodedPassword)).isTrue();
    }

    @ParameterizedTest
    @ValueAutoSource(strings = {
        "invalid-email",
        "invalid-email@",
        "@domain",
        "invalid-email@domain.",
        "invalid-email@@domain",
    })
    void 잘못된_형식의_이메일_주소를_사용해_요청하면_400_상태코드를_반환한다(
        String email,
        String password
    ) {
        // Arrange
        String path = "/api/consumer/signup";
        SignUp signUp = new SignUp(email, password);

        // Act
        ResponseEntity<Void> response = client.postForEntity(path, signUp, Void.class);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }
}

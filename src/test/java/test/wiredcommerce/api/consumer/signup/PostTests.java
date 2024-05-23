package test.wiredcommerce.api.consumer.signup;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import wiredcommerce.CommerceApplication;
import wiredcommerce.consumer.command.SignUp;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = CommerceApplication.class
)
@DisplayName("POST /api/consumer/signup")
public record PostTests(@Autowired TestRestTemplate client) {

    @Test
    void 올바른_정보를_사용해_요청하면_성공_상태코드를_반환한다() {
        // Arrange
        String path = "/api/consumer/signup";
        var command = new SignUp("user@test.com", "my password");

        // Act
        ResponseEntity<Void> response = client.postForEntity(path, command, Void.class);

        // Assert
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void 존재하는_이메일_주소를_사용해_요청하면_400_상태코드를_반환한다() {
        // Arrange
        String path = "/api/consumer/signup";
        String email = "user@test.com";
        String password1 = "my password 1";
        String password2 = "my password 2";
        client.postForEntity(path, new SignUp(email, password1), Void.class);

        // Act
        ResponseEntity<Void> response = client.postForEntity(
            path,
            new SignUp(email, password2),
            Void.class
        );

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }
}

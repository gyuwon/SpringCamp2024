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
}

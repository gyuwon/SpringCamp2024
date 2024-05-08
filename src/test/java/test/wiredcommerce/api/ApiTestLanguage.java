package test.wiredcommerce.api;

import java.util.Objects;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import wiredcommerce.consumer.command.SignUp;
import wiredcommerce.consumer.query.IssueToken;
import wiredcommerce.consumer.result.TokenCarrier;

public final class ApiTestLanguage {

    public static void signUp(TestRestTemplate client, SignUp signUp) {
        client.postForEntity("/api/consumer/signup", signUp, Void.class);
    }

    public static String issueToken(TestRestTemplate client, String email, String password) {
        ResponseEntity<TokenCarrier> response = client.postForEntity(
            "/api/consumer/issue-token",
            new IssueToken(email, password),
            TokenCarrier.class
        );
        return Objects.requireNonNull(response.getBody()).token();
    }
}

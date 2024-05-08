package test.wiredcommerce.api;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.RequestEntity;
import wiredcommerce.consumer.command.ChangePhoneNumber;
import wiredcommerce.consumer.command.SignUp;
import wiredcommerce.consumer.query.IssueToken;
import wiredcommerce.consumer.result.TokenCarrier;
import wiredcommerce.consumer.view.ConsumerView;

public final class ApiTestLanguage {

    public static void signUp(TestRestTemplate client, SignUp signUp) {
        client.postForObject("/api/consumer/signup", signUp, Void.class);
    }

    public static String issueToken(TestRestTemplate client, String email, String password) {
        TokenCarrier carrier = client.postForObject(
            "/api/consumer/issue-token",
            new IssueToken(email, password),
            TokenCarrier.class
        );
        return carrier.token();
    }

    public static ConsumerView meAsConsumer(TestRestTemplate client, String token) {
        RequestEntity<Void> request = RequestEntity
            .get("/api/consumer/me")
            .header("Authorization", "Bearer " + token)
            .build();
        return client.exchange(request, ConsumerView.class).getBody();
    }

    public static void changePhoneNumber(
        TestRestTemplate client,
        String token,
        ChangePhoneNumber command
    ) {
        RequestEntity<ChangePhoneNumber> request = RequestEntity
            .post("/api/consumer/change-phone-number")
            .header("Authorization", "Bearer " + token)
            .body(command);
        client.exchange(request, Void.class);
    }
}

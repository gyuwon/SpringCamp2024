package test.wiredcommerce.api;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.RequestEntity;
import wiredcommerce.consumer.command.ChangePhoneNumber;
import wiredcommerce.consumer.view.ConsumerView;
import wiredcommerce.query.IssueToken;
import wiredcommerce.result.TokenCarrier;

public final class ApiTestLanguage {

    public static void signUp(
        TestRestTemplate client,
        wiredcommerce.consumer.command.SignUp signUp
    ) {
        client.postForObject("/api/consumer/signup", signUp, Void.class);
    }

    public static String issueConsumerToken(
        TestRestTemplate client,
        String email,
        String password
    ) {
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

    public static void signUp(
        TestRestTemplate client,
        wiredcommerce.seller.command.SignUp signUp
    ) {
        client.postForObject("/api/seller/signup", signUp, Void.class);
    }

    public static String issueSellerToken(
        TestRestTemplate client,
        String email,
        String password
    ) {
        TokenCarrier carrier = client.postForObject(
            "/api/seller/issue-token",
            new IssueToken(email, password),
            TokenCarrier.class
        );
        return carrier.token();
    }
}

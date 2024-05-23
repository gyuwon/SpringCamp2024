package test.wiredcommerce.api;

import java.net.URI;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import wiredcommerce.consumer.command.ChangePhoneNumber;
import wiredcommerce.consumer.view.ConsumerSelfView;
import wiredcommerce.query.IssueToken;
import wiredcommerce.result.TokenCarrier;
import wiredcommerce.seller.command.AddNewProduct;
import wiredcommerce.seller.view.SellerSelfView;
import wiredcommerce.view.Page;
import wiredcommerce.view.ProductView;

import static org.springframework.http.RequestEntity.get;

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

    public static ConsumerSelfView meAsConsumer(TestRestTemplate client, String token) {
        RequestEntity<Void> request = get("/api/consumer/me")
            .header("Authorization", "Bearer " + token)
            .build();
        return client.exchange(request, ConsumerSelfView.class).getBody();
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

    public static SellerSelfView meAsSeller(TestRestTemplate client, String token) {
        RequestEntity<Void> request = get("/api/seller/me")
            .header("Authorization", "Bearer " + token)
            .build();
        return client.exchange(request, SellerSelfView.class).getBody();
    }

    public static void addProduct(
        TestRestTemplate client,
        String token,
        AddNewProduct addProduct
    ) {
        RequestEntity<AddNewProduct> request = RequestEntity
            .post("/api/seller/products")
            .header("Authorization", "Bearer " + token)
            .body(addProduct);
        client.exchange(request, Void.class);
    }

    public static Page<ProductView> getProducts(
        TestRestTemplate client,
        String accessToken,
        Integer minPriceInclusive,
        Integer maxPriceExclusive,
        String continuationToken
    ) {
        UriComponentsBuilder builder = UriComponentsBuilder
            .fromPath("/api/consumer/products");

        if (minPriceInclusive != null) {
            builder.queryParam("min-price-inclusive", minPriceInclusive);
        }

        if (maxPriceExclusive != null) {
            builder.queryParam("max-price-exclusive", maxPriceExclusive);
        }

        if (continuationToken != null) {
            builder.queryParam("continuation-token", continuationToken);
        }

        URI uri = builder.build().toUri();

        ResponseEntity<Page<ProductView>> response = client.exchange(
            get(uri).header("Authorization", "Bearer " + accessToken).build(),
            new ParameterizedTypeReference<>() { }
        );

        return response.getBody();
    }
}

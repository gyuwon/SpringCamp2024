package test.wiredcommerce.api.consumer.changephonenumber;

import autoparams.AutoSource;
import autoparams.BrakeBeforeAnnotation;
import autoparams.MethodAutoSource;
import autoparams.customization.Customization;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import test.wiredcommerce.api.consumer.ChangePhoneNumberGenerator;
import wiredcommerce.CommerceApplication;
import wiredcommerce.consumer.command.ChangePhoneNumber;
import wiredcommerce.consumer.command.SignUp;
import wiredcommerce.consumer.view.ConsumerView;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static test.wiredcommerce.api.ApiTestLanguage.changePhoneNumber;
import static test.wiredcommerce.api.ApiTestLanguage.issueToken;
import static test.wiredcommerce.api.ApiTestLanguage.meAsConsumer;
import static test.wiredcommerce.api.ApiTestLanguage.signUp;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = CommerceApplication.class
)
@DisplayName("POST /api/consumer/change-phone-number")
public class PostTests {

    @ParameterizedTest
    @AutoSource
    @Customization(ChangePhoneNumberGenerator.class)
    @BrakeBeforeAnnotation(Autowired.class)
    void 올바르게_전화번호를_변경한다(
        SignUp signUp,
        ChangePhoneNumber changePhoneNumber,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        signUp(client, signUp);
        String token = issueToken(client, signUp.email(), signUp.password());

        // Act
        RequestEntity<ChangePhoneNumber> request = RequestEntity
            .post("/api/consumer/change-phone-number")
            .header("Authorization", "Bearer " + token)
            .body(changePhoneNumber);
        client.exchange(request, Void.class);

        // Assert
        ConsumerView consumer = meAsConsumer(client, token);
        assertThat(consumer.phoneNumber()).isEqualTo(changePhoneNumber.phoneNumber());
    }

    @ParameterizedTest
    @MethodAutoSource("wrongPhoneNumbers")
    @Customization(ChangePhoneNumberGenerator.class)
    @BrakeBeforeAnnotation(Autowired.class)
    void 전화번호가_유효하지_않으면_전화번호를_변경하지_않는다(
        String wrongPhoneNumber,
        SignUp signUp,
        ChangePhoneNumber changePhoneNumber,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        signUp(client, signUp);
        String token = issueToken(client, signUp.email(), signUp.password());
        changePhoneNumber(client, token, changePhoneNumber);

        // Act
        RequestEntity<ChangePhoneNumber> request = RequestEntity
            .post("/api/consumer/change-phone-number")
            .header("Authorization", "Bearer " + token)
            .body(new ChangePhoneNumber(wrongPhoneNumber));
        client.exchange(request, Void.class);

        // Assert
        ConsumerView consumer = meAsConsumer(client, token);
        assertThat(consumer.phoneNumber()).isEqualTo(changePhoneNumber.phoneNumber());
    }

    @ParameterizedTest
    @MethodAutoSource("wrongPhoneNumbers")
    @BrakeBeforeAnnotation(Autowired.class)
    void 전화번호가_유효하지_않으면_400_상태코드를_반환한다(
        String wrongPhoneNumber,
        SignUp signUp,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        signUp(client, signUp);
        String token = issueToken(client, signUp.email(), signUp.password());

        // Act
        RequestEntity<ChangePhoneNumber> request = RequestEntity
            .post("/api/consumer/change-phone-number")
            .header("Authorization", "Bearer " + token)
            .body(new ChangePhoneNumber(wrongPhoneNumber));
        ResponseEntity<Void> response = client.exchange(request, Void.class);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(400);
    }

    @ParameterizedTest
    @AutoSource
    @Customization(ChangePhoneNumberGenerator.class)
    @BrakeBeforeAnnotation(Autowired.class)
    void 전화번호가_유효하면_204_상태코드를_반환한다(
        SignUp signUp,
        ChangePhoneNumber changePhoneNumber,
        @Autowired TestRestTemplate client
    ) {
        // Arrange
        signUp(client, signUp);
        String token = issueToken(client, signUp.email(), signUp.password());

        // Act
        RequestEntity<ChangePhoneNumber> request = RequestEntity
            .post("/api/consumer/change-phone-number")
            .header("Authorization", "Bearer " + token)
            .body(changePhoneNumber);
        ResponseEntity<Void> response = client.exchange(request, Void.class);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(204);
    }

    @SuppressWarnings("unused")
    private static Stream<Arguments> wrongPhoneNumbers() {
        return Stream.of(
            arguments("-1234-5678"),
            arguments("1234-5678"),
            arguments("-010-1234-5678"),
            arguments("010-1234-5678-"),
            arguments("invalid phone number")
        );
    }
}

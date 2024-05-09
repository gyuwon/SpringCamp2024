package test.wiredcommerce.api;

import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import static org.junit.jupiter.params.provider.Arguments.arguments;

@SuppressWarnings("unused")
public class TestArguments {

    public static Stream<Arguments> invalidEmails() {
        return Stream.of(
            arguments("invalid-email"),
            arguments("invalid-email@"),
            arguments("@domain"),
            arguments("invalid-email@domain."),
            arguments("invalid-email@@domain")
        );
    }

    public static Stream<Arguments> invalidPhoneNumbers() {
        return Stream.of(
            arguments("-1234-5678"),
            arguments("1234-5678"),
            arguments("-010-1234-5678"),
            arguments("010-1234-5678-"),
            arguments("invalid phone number")
        );
    }
}

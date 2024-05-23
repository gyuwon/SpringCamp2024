package wiredcommerce.seller.command;

public record SignUp(
    String email,
    String username,
    String password,
    String phoneNumber
) { }

package wiredcommerce.security;

public interface JwtComposer {

    String compose(String subject);
}

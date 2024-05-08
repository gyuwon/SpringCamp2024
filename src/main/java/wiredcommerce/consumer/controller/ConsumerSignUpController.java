package wiredcommerce.consumer.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumerSignUpController {

    @PostMapping("/api/consumer/signup")
    public void signUp() {
    }
}

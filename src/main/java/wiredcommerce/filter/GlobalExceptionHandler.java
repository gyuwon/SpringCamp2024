package wiredcommerce.filter;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import wiredcommerce.data.InvalidPrincipalException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @SuppressWarnings("unused")
    @ExceptionHandler(InvalidPrincipalException.class)
    public ResponseEntity<Void> handle(InvalidPrincipalException exception) {
        return ResponseEntity.status(401).build();
    }
}

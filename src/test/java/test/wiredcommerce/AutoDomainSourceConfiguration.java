package test.wiredcommerce;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import autoparams.BrakeBeforeAnnotation;
import autoparams.customization.Customization;
import org.springframework.beans.factory.annotation.Autowired;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Customization({
    PhoneNumberGenerator.class,
    PriceGenerator.class,
    QuantityGenerator.class,
})
@BrakeBeforeAnnotation(Autowired.class)
public @interface AutoDomainSourceConfiguration {
}

package test.wiredcommerce.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import autoparams.BrakeBeforeAnnotation;
import autoparams.customization.Customization;
import org.springframework.beans.factory.annotation.Autowired;
import test.wiredcommerce.api.consumer.ChangePhoneNumberGenerator;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Customization({ ChangePhoneNumberGenerator.class, PhoneNumberGenerator.class })
@BrakeBeforeAnnotation(Autowired.class)
public @interface AutoDomainSourceConfiguration {
}

package test.wiredcommerce.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import autoparams.AutoSource;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@AutoSource
@AutoDomainSourceConfiguration
public @interface AutoDomainSource {
}

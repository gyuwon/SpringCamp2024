package test.wiredcommerce;

import java.util.concurrent.ThreadLocalRandom;

import autoparams.ObjectQuery;
import autoparams.ParameterQuery;
import autoparams.ResolutionContext;
import autoparams.generator.ObjectContainer;
import autoparams.generator.ObjectGenerator;

public class PhoneNumberGenerator implements ObjectGenerator {

    @Override
    public ObjectContainer generate(ObjectQuery query, ResolutionContext context) {
        return query.getType().equals(String.class)
            && query instanceof ParameterQuery parameterQuery
            ? generator(parameterQuery)
            : ObjectContainer.EMPTY;
    }

    private ObjectContainer generator(ParameterQuery query) {
        return query
            .getParameterName()
            .map(String::toLowerCase)
            .filter(name -> name.endsWith("phonenumber"))
            .map(name -> ThreadLocalRandom.current())
            .map(random -> "010-"
                + random.nextInt(1000, 10000) + "-"
                + random.nextInt(1000, 10000))
            .map(ObjectContainer::new)
            .orElse(ObjectContainer.EMPTY);
    }
}

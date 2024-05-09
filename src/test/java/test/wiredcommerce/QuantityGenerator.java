package test.wiredcommerce;

import java.util.concurrent.ThreadLocalRandom;

import autoparams.ObjectQuery;
import autoparams.ParameterQuery;
import autoparams.ResolutionContext;
import autoparams.generator.ObjectContainer;
import autoparams.generator.ObjectGenerator;

public class QuantityGenerator implements ObjectGenerator {

    @Override
    public ObjectContainer generate(ObjectQuery query, ResolutionContext context) {
        return query.getType().equals(int.class)
            && query instanceof ParameterQuery parameterQuery
            ? generate(parameterQuery)
            : ObjectContainer.EMPTY;
    }

    private ObjectContainer generate(ParameterQuery query) {
        return query
            .getParameterName()
            .map(String::toLowerCase)
            .filter(name -> name.endsWith("quantity"))
            .map(name -> ThreadLocalRandom.current())
            .map(random -> random.nextInt(10, 100))
            .map(ObjectContainer::new)
            .orElse(ObjectContainer.EMPTY);
    }
}

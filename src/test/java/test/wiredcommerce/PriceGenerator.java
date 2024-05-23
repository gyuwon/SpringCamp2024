package test.wiredcommerce;

import java.util.concurrent.ThreadLocalRandom;

import autoparams.ObjectQuery;
import autoparams.ParameterQuery;
import autoparams.ResolutionContext;
import autoparams.generator.ObjectContainer;
import autoparams.generator.ObjectGenerator;

public class PriceGenerator implements ObjectGenerator {

    private final int minInclusive;
    private final int maxExclusive;

    public PriceGenerator(int minInclusive, int maxExclusive) {
        this.minInclusive = minInclusive;
        this.maxExclusive = maxExclusive;
    }

    @SuppressWarnings("unused")
    public PriceGenerator() {
        this(10000, 1000000);
    }

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
            .filter(name -> name.endsWith("price"))
            .map(name -> ThreadLocalRandom.current().nextInt(minInclusive, maxExclusive))
            .map(ObjectContainer::new)
            .orElse(ObjectContainer.EMPTY);
    }

    public static PriceGenerator price(int minInclusive, int maxExclusive) {
        return new PriceGenerator(minInclusive, maxExclusive);
    }
}

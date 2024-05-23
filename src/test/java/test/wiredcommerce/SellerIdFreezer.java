package test.wiredcommerce;

import autoparams.ObjectQuery;
import autoparams.ParameterQuery;
import autoparams.ResolutionContext;
import autoparams.generator.ObjectContainer;
import autoparams.generator.ObjectGenerator;
import wiredcommerce.data.SellerEntity;

public record SellerIdFreezer(long sellerId) implements ObjectGenerator {

    @Override
    public ObjectContainer generate(ObjectQuery query, ResolutionContext context) {
        return query.getType().equals(long.class)
            && query instanceof ParameterQuery parameterQuery
            ? generate(parameterQuery)
            : ObjectContainer.EMPTY;
    }

    private ObjectContainer generate(ParameterQuery query) {
        return query
            .getParameterName()
            .filter(name -> name.equals("sellerId"))
            .map(name -> new ObjectContainer(sellerId))
            .orElse(ObjectContainer.EMPTY);
    }

    public static SellerIdFreezer freezeSellerId(SellerEntity seller) {
        return new SellerIdFreezer(seller.getId());
    }
}

package wiredcommerce.querymodel;

import java.util.List;

import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import wiredcommerce.data.ProductEntity;
import wiredcommerce.query.GetProducts;
import wiredcommerce.view.Page;
import wiredcommerce.view.ProductView;

import static java.lang.Long.parseLong;

@AllArgsConstructor
public class GetProductsQueryProcessor {

    private static final int PAGE_SIZE = 10;
    private static final String QUERY_STRING = """
        SELECT new wiredcommerce.querymodel.ProductWithSeller(p, s)
        FROM ProductEntity p
        JOIN SellerEntity s ON p.sellerId = s.id
        WHERE (:lastEvaluatedId IS NULL OR p.id < :lastEvaluatedId)
        AND (:minPriceInclusive IS NULL OR p.price >= :minPriceInclusive)
        AND (:maxPriceExclusive IS NULL OR p.price < :maxPriceExclusive)
        ORDER BY p.id DESC
        """;

    private final EntityManager entityManager;

    public Page<ProductView> process(GetProducts query) {
        return putOnPage(fetchData(query)).map(ProductWithSeller::toView);
    }

    private List<ProductWithSeller> fetchData(GetProducts query) {
        return entityManager
            .createQuery(QUERY_STRING, ProductWithSeller.class)
            .setParameter("lastEvaluatedId", getLastEvaluatedId(query))
            .setParameter("minPriceInclusive", query.minPriceInclusive())
            .setParameter("maxPriceExclusive", query.maxPriceExclusive())
            .setMaxResults(PAGE_SIZE + 1)
            .getResultList();
    }

    private static Long getLastEvaluatedId(GetProducts query) {
        String continuationToken = query.continuationToken();
        return continuationToken == null ? null : parseLong(continuationToken);
    }

    private static Page<ProductWithSeller> putOnPage(
        List<ProductWithSeller> results
    ) {
        if (results.size() > PAGE_SIZE) {
            ProductEntity lastEvaluated = results.get(PAGE_SIZE - 1).product();
            String continuationToken = lastEvaluated.getId().toString();
            return new Page<>(results.subList(0, PAGE_SIZE), continuationToken);
        }

        return new Page<>(results, null);
    }
}

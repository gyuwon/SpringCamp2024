package wiredcommerce.querymodel;

import java.util.List;

import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import wiredcommerce.query.GetProductsOfSeller;
import wiredcommerce.view.ProductView;

@AllArgsConstructor
public class GetProductsOfSellerQueryProcessor {

    private static final String QUERY_STRING = """
        SELECT new wiredcommerce.querymodel.ProductWithSeller(p, s)
        FROM ProductEntity p
        JOIN SellerEntity s ON p.sellerId = s.id
        WHERE p.sellerId = :sellerId
        """;

    private final EntityManager entityManager;

    public List<ProductView> process(GetProductsOfSeller query) {
        List<ProductWithSeller> results = fetchData(query);
        return results.stream().map(ProductWithSeller::toView).toList();
    }

    private List<ProductWithSeller> fetchData(GetProductsOfSeller query) {
        return entityManager
            .createQuery(QUERY_STRING, ProductWithSeller.class)
            .setParameter("sellerId", query.sellerId())
            .getResultList();
    }
}

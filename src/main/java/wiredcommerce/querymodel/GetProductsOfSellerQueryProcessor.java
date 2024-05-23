package wiredcommerce.querymodel;

import java.util.List;

import jakarta.persistence.EntityManager;
import wiredcommerce.query.GetProductsOfSeller;
import wiredcommerce.view.ProductView;

public class GetProductsOfSellerQueryProcessor {

    private final EntityManager entityManager;

    public GetProductsOfSellerQueryProcessor(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<ProductView> process(GetProductsOfSeller query) {
        String queryString = """
            SELECT new wiredcommerce.querymodel.ProductWithSeller(p, s)
            FROM ProductEntity p
            JOIN SellerEntity s ON p.sellerId = s.id
            WHERE p.sellerId = :sellerId
            """;

        List<ProductWithSeller> results = entityManager
            .createQuery(queryString, ProductWithSeller.class)
            .setParameter("sellerId", query.sellerId())
            .getResultList();

        return results.stream().map(ProductWithSeller::toView).toList();
    }
}

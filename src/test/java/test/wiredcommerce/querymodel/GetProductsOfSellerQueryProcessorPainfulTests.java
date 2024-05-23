package test.wiredcommerce.querymodel;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wiredcommerce.CommerceApplication;
import wiredcommerce.commandmodel.Product;
import wiredcommerce.data.ProductJpaRepository;
import wiredcommerce.data.SellerEntity;
import wiredcommerce.data.SellerJpaRepository;
import wiredcommerce.query.GetProductsOfSeller;
import wiredcommerce.querymodel.GetProductsOfSellerQueryProcessor;
import wiredcommerce.view.ProductView;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = CommerceApplication.class)
public class GetProductsOfSellerQueryProcessorPainfulTests {

    @Test
    void 다른_판매자가_소유한_상품은_반환하지_않는다(
        @Autowired SellerJpaRepository sellerRepository,
        @Autowired ProductJpaRepository productRepository,
        @Autowired EntityManager entityManager
    ) {
        // Arrange
        var random = new Random();

        SellerEntity seller = SellerEntity
            .builder()
            .email(UUID.randomUUID() + "@test.com")
            .username(UUID.randomUUID().toString())
            .encodedPassword("password")
            .phoneNumber("010-" + random.nextInt(1000, 10000) + "-" + random.nextInt(1000, 10000))
            .build();

        sellerRepository.save(seller);

        productRepository.addProduct(new Product(
            UUID.randomUUID(),
            seller.getId(),
            "상품",
            "상품 설명",
            10000,
            100
        ));

        SellerEntity anotherSeller = SellerEntity
            .builder()
            .email(UUID.randomUUID() + "@test.com")
            .username(UUID.randomUUID().toString())
            .encodedPassword("password")
            .phoneNumber("010-" + random.nextInt(1000, 10000) + "-" + random.nextInt(1000, 10000))
            .build();

        sellerRepository.save(anotherSeller);

        productRepository.addProduct(new Product(
            UUID.randomUUID(),
            anotherSeller.getId(),
            "다른 상품",
            "다른 상품 설명",
            20000,
            200
        ));

        var sut = new GetProductsOfSellerQueryProcessor(entityManager);

        // Act
        var query = new GetProductsOfSeller(seller.getId());
        List<ProductView> actual = sut.process(query);

        // Assert
        assertThat(actual).extracting(x -> x.seller().id()).containsExactly(seller.getId());
    }
}

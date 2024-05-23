package wiredcommerce.querymodel;

import wiredcommerce.data.ProductEntity;
import wiredcommerce.data.SellerEntity;
import wiredcommerce.view.ProductView;
import wiredcommerce.view.SellerView;

record ProductWithSeller(ProductEntity product, SellerEntity seller) {

    public ProductView toView() {
        return new ProductView(
            product.getProductId(),
            new SellerView(
                seller.getId(),
                seller.getUsername()
            ),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStockQuantity()
        );
    }
}

package test.wiredcommerce.commandmodel;

import java.util.ArrayList;
import java.util.List;

import wiredcommerce.commandmodel.Product;
import wiredcommerce.commandmodel.ProductRepository;

import static java.util.Collections.unmodifiableList;

public class InMemoryProductRepository implements ProductRepository {

    private final List<Product> products = new ArrayList<>();
    private final List<Product> productsView = unmodifiableList(products);

    @Override
    public void addProduct(Product product) {
        products.add(product);
    }

    public List<Product> getProducts() {
        return productsView;
    }
}

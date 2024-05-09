package wiredcommerce.commandmodel;

import java.util.UUID;

import lombok.AllArgsConstructor;
import wiredcommerce.command.AddProduct;

@AllArgsConstructor
public final class AddProductCommandExecutor {

    private final ProductRepository repository;

    public void execute(UUID id, AddProduct command) {
        if (command.price() < 1000) {
            throw new InvariantViolationException("가격은 1000보다 작을 수 없습니다.");
        }

        if (command.stockQuantity() < 0) {
            throw new InvariantViolationException("재고수량은 0보다 작을 수 없습니다.");
        }

        var product = new Product(
            id,
            command.sellerId(),
            command.name(),
            command.description(),
            command.price(),
            command.stockQuantity()
        );

        repository.addProduct(product);
    }
}

package test.wiredcommerce.commandmodel;

import java.util.UUID;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import autoparams.customization.Customization;
import autoparams.customization.Freeze;
import autoparams.mockito.MockitoCustomizer;
import org.junit.jupiter.params.ParameterizedTest;
import test.wiredcommerce.AutoDomainSource;
import wiredcommerce.command.AddProduct;
import wiredcommerce.commandmodel.AddProductCommandExecutor;
import wiredcommerce.commandmodel.InvariantViolationException;
import wiredcommerce.commandmodel.Product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AddProductCommandExecutorTests {

    @ParameterizedTest
    @AutoDomainSource
    void 명령을_실행하면_상품이_등록된다(
        @Freeze(byImplementedInterfaces = true) InMemoryProductRepository repository,
        AddProductCommandExecutor sut,
        UUID id,
        AddProduct command
    ) {
        // Act
        sut.execute(id, command);

        // Assert
        assertThat(repository.getProducts()).hasSize(1);
        assertThat(repository.getProducts().getFirst().id()).isEqualTo(id);
    }

    @ParameterizedTest
    @AutoDomainSource
    void 상품_속성을_올바르게_초기화한다(
        @Freeze(byImplementedInterfaces = true) InMemoryProductRepository repository,
        AddProductCommandExecutor sut,
        UUID id,
        AddProduct command
    ) {
        // Act
        sut.execute(id, command);

        // Assert
        Product actual = repository.getProducts().getFirst();
        assertThat(actual.sellerId()).isEqualTo(command.sellerId());
        assertThat(actual.name()).isEqualTo(command.name());
        assertThat(actual.description()).isEqualTo(command.description());
        assertThat(actual.price()).isEqualTo(command.price());
        assertThat(actual.stockQuantity()).isEqualTo(command.stockQuantity());
    }

    @ParameterizedTest
    @AutoDomainSource
    @Customization(MockitoCustomizer.class)
    void 상품_등록시_가격이_1000보다_작으면_예외가_발생한다(
        AddProductCommandExecutor sut,
        UUID id,
        long sellerId,
        String name,
        String description,
        @Min(-10000) @Max(999) int violation,
        int stockQuantity
    ) {
        // Arrange
        var command = new AddProduct(
            sellerId,
            name,
            description,
            violation,
            stockQuantity
        );

        // Act & Assert
        assertThatThrownBy(() -> sut.execute(id, command))
            .isInstanceOf(InvariantViolationException.class);
    }

    @ParameterizedTest
    @AutoDomainSource
    @Customization(MockitoCustomizer.class)
    void 상품_등록시_재고수량이_0보다_작으면_예외가_발생한다(
        AddProductCommandExecutor sut,
        UUID id,
        long sellerId,
        String name,
        String description,
        int price,
        @Min(-10000) @Max(-1) int violation
    ) {
        // Arrange
        var command = new AddProduct(
            sellerId,
            name,
            description,
            price,
            violation
        );

        // Act & Assert
        assertThatThrownBy(() -> sut.execute(id, command))
            .isInstanceOf(InvariantViolationException.class);
    }
}

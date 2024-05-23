package wiredcommerce.view;

import java.util.function.Function;

import static java.util.stream.StreamSupport.stream;

public record Page<T>(Iterable<T> items, String continuationToken) {

    public <R> Page<R> map(Function<? super T, R> mapper) {
        return new Page<>(
            stream(items.spliterator(), false).map(mapper).toList(),
            continuationToken
        );
    }
}

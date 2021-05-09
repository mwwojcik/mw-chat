package mw.chat.reactor.operators;

import com.github.javafaker.Faker;
import mw.chat.reactor.DefaultSimpleSubscriber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class SwitchIfEmpty {

    private Faker faker = new Faker();

    @DisplayName("Should emit signals from alternative signal source when main stream is empty")
    @Test
    void shouldEmitSignalsFromAlternativeSignalSourceWhenMainStreamIsEmpty() {
        Flux.range(1, 10).filter(it -> it > 15).switchIfEmpty(fallback()).subscribe(DefaultSimpleSubscriber.create());

    }

    private Flux<Integer> fallback() {
        return Flux.create(sink->{
            for (int i = 0; i < 10; i++) {
                sink.next(faker.random().nextInt(100,200));
            }
            sink.complete();
        });
    }
}

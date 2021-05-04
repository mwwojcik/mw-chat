package mw.chat.reactor.operators;

import com.github.javafaker.Faker;
import mw.chat.reactor.DefaultSimpleSubscriber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class ReactiveOperatorsTest {

    private Faker faker = Faker.instance();

    @DisplayName("Should emit only odd numbers - HANDLE operator")
    @Test
    void shouldEmitOnlyOddNumbersViaHandleOperator() {
        Flux.range(1, 25).handle((myNumber, sink) -> {
            if (myNumber % 2 == 0) {
                sink.next(myNumber);
            }
        }).subscribe(DefaultSimpleSubscriber.create());
    }

    @DisplayName("Should emit country names until Canada occured")
    @Test
    void shouldEmitCountryNamesUntilCanadaOccured() {
        Flux.generate(sink -> sink.next(faker.country().name())).map(Object::toString).handle((s, synchronousSink) -> {
            synchronousSink.next(s);
            if (s.equalsIgnoreCase("canada")) {
                synchronousSink.complete();
            }
        }).subscribe(DefaultSimpleSubscriber.create());
    }

}

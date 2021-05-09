package mw.chat.reactor.operators;

import mw.chat.reactor.DefaultSimpleSubscriber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class IfEmptyOperatorTest {

    @DisplayName("Should emit default value when stream is empty")
    @Test
    void shouldEmitDefaultValueWhenStreamIsEmpty() {
        Flux.range(1, 10).filter(it -> it > 15).defaultIfEmpty(-100).subscribe(DefaultSimpleSubscriber.create());
    }
}

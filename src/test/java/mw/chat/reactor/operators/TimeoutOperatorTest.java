package mw.chat.reactor.operators;

import java.time.Duration;
import mw.chat.reactor.DefaultSimpleSubscriber;
import mw.chat.reactor.Sleeper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class TimeoutOperatorTest {

    @DisplayName("Should terminate too slow emitter")
    @Test
    void shouldTerminateTooSlowEmitter() {
        slowSignalSource().timeout(Duration.ofSeconds(3)).subscribe(DefaultSimpleSubscriber.create());
        Sleeper.sleepSecconds(5);
    }

    @DisplayName("Should emit fallback signals when timeout is reached")
    @Test
    void shouldEmitFallbackSignalsWhenTimeoutIsReached() {
        slowSignalSource().timeout(Duration.ofSeconds(3), fallback()).subscribe(DefaultSimpleSubscriber.create());
        Sleeper.sleepSecconds(5);
    }

    /**
     * This method is run only in case of error in main emiter
     */
    private Flux<Integer> fallback() {
        return Flux.range(1, 15).delayElements(Duration.ofMillis(20));
    }

    public Flux<Integer> slowSignalSource() {
        /*For 5 seconds any output from this method*/
        return Flux.range(1, 10).delayElements(Duration.ofSeconds(5));
    }


}

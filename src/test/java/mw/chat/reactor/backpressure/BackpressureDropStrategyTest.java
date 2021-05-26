package mw.chat.reactor.backpressure;

import lombok.extern.slf4j.Slf4j;
import mw.chat.reactor.DefaultSimpleSubscriber;
import mw.chat.reactor.Sleeper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.util.concurrent.Queues;

@Slf4j
public class BackpressureDropStrategyTest {

    @DisplayName("Should emit complete signal when queue is overloaded")
    @Test
    void shouldEmitCompleteSignalWhenQueueIsOverloaded() {

        Flux.create(fluxSink -> {
            for (int i = 1; i < 501; i++) {
                fluxSink.next(i);
                log.info("Pushed=>" + i);
            }
            fluxSink.complete();
        }).onBackpressureDrop()
            .publishOn(Schedulers.boundedElastic())
            .doOnNext(i -> {
            Sleeper.sleepMillis(10);
            log.info("Received=>" + i);
        }).subscribe(DefaultSimpleSubscriber.create());

        Sleeper.sleepSecconds(50);
    }
}

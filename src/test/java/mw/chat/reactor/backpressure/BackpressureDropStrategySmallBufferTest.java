package mw.chat.reactor.backpressure;

import lombok.extern.slf4j.Slf4j;
import mw.chat.reactor.DefaultSimpleSubscriber;
import mw.chat.reactor.Sleeper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class BackpressureDropStrategySmallBufferTest {

    @DisplayName("Should emit complete signal when queue is overloaded")
    @Test
    void shouldEmitCompleteSignalWhenQueueIsOverloaded() {
        System.setProperty("reactor.bufferSize.small", "16");
        Flux.create(fluxSink -> {
            for (int i = 1; i < 200; i++) {
                fluxSink.next(i);
                log.info("Pushed=>" + i);
                Sleeper.sleepMillis(1);
            }
            fluxSink.complete();
        }).onBackpressureDrop()
            .publishOn(Schedulers.boundedElastic())
            .doOnNext(i -> {
            Sleeper.sleepMillis(10);
        }).subscribe(DefaultSimpleSubscriber.create());

        Sleeper.sleepSecconds(60);
    }
}

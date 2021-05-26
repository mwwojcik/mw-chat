package mw.chat.reactor.backpressure;

import lombok.extern.slf4j.Slf4j;
import mw.chat.reactor.DefaultSimpleSubscriber;
import mw.chat.reactor.Sleeper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class BackpressureDefaultStrategyTest {

    @DisplayName("Shuld buffer in memory not conusumed events")
    @Test
    void shuldBufferInMemoryNotConusumedEvents() {
        Flux.create(fluxSink -> {
            for (int i = 1; i < 501; i++) {
                fluxSink.next(i);
                log.info("Pushed=>" + i);
            }
            fluxSink.complete();
        }).publishOn(Schedulers.boundedElastic())
            .doOnNext(i->{
                Sleeper.sleepSecconds(1);
                log.info("Received=>"+i);
            })
            .subscribe(DefaultSimpleSubscriber.create());

        Sleeper.sleepSecconds(5);
    }
}

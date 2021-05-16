package mw.chat.reactor.threading;

import lombok.extern.slf4j.Slf4j;
import mw.chat.reactor.Sleeper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class SubscribeOnThreadPoolsTest {

    @DisplayName("Should retrieve all signals in the same thread despite pooling")
    @Test
    void shouldRetrieveAllSignalsInTheSameThreadDespitePooling() {

        Flux flux = Flux.create(fluxSink -> {
            printThreadMessage("create");
            for (int i = 0; i < 10; i++) {
                fluxSink.next(1);
            }
            fluxSink.complete();
        }).subscribeOn(Schedulers.parallel()).doOnNext(s -> printThreadMessage("next"));

        flux.subscribe(s -> printThreadMessage("sub"));

        Sleeper.sleepSecconds(3);
    }

    private void printThreadMessage(String msg) {
        log.info(String.format("%s=>%s", Thread.currentThread().getName(), msg));
    }
}

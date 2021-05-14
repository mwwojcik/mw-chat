package mw.chat.reactor.threading;

import lombok.extern.slf4j.Slf4j;
import mw.chat.reactor.Sleeper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

@Slf4j
public class DefaultThreadingTest {

    @DisplayName("Should execute all pipeline tasks in the same thread")
    @Test
    void shouldExecuteAllPipelineTasksInTheSameThread() {
        Flux flux = Flux.create(fluxSink -> {
            printThreadMessage("create");
            fluxSink.next(1);
        }).doOnNext(i -> printThreadMessage("next"));

        flux.subscribe(i -> printThreadMessage("Subscribe"));

    }

    @DisplayName("Should execute all pipeline tasks in subscriber thread")
    @Test
    void shouldExecuteAllPipelineTasksInSubscriberThread() {
        Flux flux = Flux.create(fluxSink -> {
            printThreadMessage("create");
            fluxSink.next(1);
        }).doOnNext(i -> printThreadMessage("next"));

        Runnable r = () -> flux.subscribe(i -> printThreadMessage("subscribe"));

        for (int i = 0; i < 2; i++) {
            new Thread(r).start();
            Sleeper.sleepSecconds(1);
        }
        Sleeper.sleepSecconds(5);
    }

    private void printThreadMessage(String msg) {
        log.info(String.format("%s=>%s", Thread.currentThread().getName(), msg));
    }
}

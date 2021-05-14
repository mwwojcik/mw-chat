package mw.chat.reactor.threading;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class SubscribeOnOperatorTest {

    @DisplayName("Should switch execution to pooled thread after subscribeOn operator")
    @Test
    void shouldSwitchExecutionToPooledThreadAfterSubscribeOnOperator() {
        Flux.create(fluxSink -> {
            printThreadMessage("create");
            fluxSink.next(1);
        })
            .doFirst(() -> printThreadMessage("second"))
            .subscribeOn(Schedulers.boundedElastic())
            .doFirst(() -> printThreadMessage("first"))
            .subscribe((v) -> printThreadMessage("subscribed"));
    }

    private void printThreadMessage(String msg) {
        log.info(String.format("%s=>%s", Thread.currentThread().getName(), msg));
    }
}

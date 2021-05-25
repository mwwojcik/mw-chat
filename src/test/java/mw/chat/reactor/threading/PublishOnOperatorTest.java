package mw.chat.reactor.threading;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Fail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class PublishOnOperatorTest {

    @DisplayName("Should switch threadpool after publish on operator")
    @Test
    void shouldSwitchThreadpoolAfterPublishOnOperator() {
        Flux.create(fluxSink -> {
            printThreadMessage("create");
            fluxSink.next(1);
        })
            .doOnNext((sink) -> printThreadMessage("first"))
            .publishOn(Schedulers.boundedElastic())
            .doOnNext((sink) -> printThreadMessage("second"))
            .subscribe((v) -> printThreadMessage("subscribed"));
    }

    private void printThreadMessage(String msg) {
        log.info(String.format("%s=>%s", Thread.currentThread().getName(), msg));
    }
}

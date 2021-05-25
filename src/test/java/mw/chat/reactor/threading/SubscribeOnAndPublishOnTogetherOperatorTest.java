package mw.chat.reactor.threading;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Fail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class SubscribeOnAndPublishOnTogetherOperatorTest {

    @DisplayName("Should switch ThreadPool after publishOn and set producer ThtreadPool by switch On ")
    @Test
    void shouldSwitchThreadPoolAfterPublishOnAndSetProducerThtreadPoolBySwitchOn() {

        Flux.create(fluxSink -> {
            printThreadMessage("create");
            fluxSink.next(1);
        })
            .doOnNext((sink) -> printThreadMessage("first"))
            .publishOn(Schedulers.parallel())
            .doOnNext((sink) -> printThreadMessage("second"))
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe((v) -> printThreadMessage("subscribed"));
    }

    private void printThreadMessage(String msg) {
        log.info(String.format("%s=>%s", Thread.currentThread().getName(), msg));
    }
}

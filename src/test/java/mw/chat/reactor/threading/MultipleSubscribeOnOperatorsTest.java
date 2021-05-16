package mw.chat.reactor.threading;

import lombok.extern.slf4j.Slf4j;
import mw.chat.reactor.Sleeper;
import org.assertj.core.api.Fail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class MultipleSubscribeOnOperatorsTest {
    @DisplayName("Should emit signals through thread from pool closer to producer")
    @Test 
    void shouldEmitSignalsThroughThreadFromPoolCloserToProducer() {
       Flux flux= Flux.create(fluxSink -> {
            printThreadMessage("create");
            fluxSink.next(1);
        })
            .subscribeOn(Schedulers.parallel())
            .doOnNext(s->printThreadMessage("next"));

        Runnable r= ()->{
            flux.subscribeOn(Schedulers.boundedElastic())
                .doOnNext(s->printThreadMessage("run"))
            .subscribe(s->printThreadMessage("sub"));
        };

        for (int i = 0; i <2; i++) {
            new Thread(r).start();
            Sleeper.sleepSecconds(1);
        }

        Sleeper.sleepSecconds(3);
     }

    private void printThreadMessage(String msg) {
        log.info(String.format("%s=>%s", Thread.currentThread().getName(), msg));
    }
}

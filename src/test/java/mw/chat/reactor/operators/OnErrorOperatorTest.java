package mw.chat.reactor.operators;

import com.github.javafaker.Faker;
import java.util.logging.Logger;
import lombok.extern.slf4j.Slf4j;
import mw.chat.reactor.DefaultSimpleSubscriber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;


public class OnErrorOperatorTest {
    Logger log=Logger.getLogger(this.getClass().getSimpleName());

    private Faker faker = new Faker();

    @DisplayName("Should throw an exeption in case of error (with cancelling Flux)")
    @Test
    void shouldThrowAnExeptionInCaseOfError() {
        callFirstSignalSource().log().map(it -> 10 / (5 - it)).subscribe(DefaultSimpleSubscriber.create());
    }

    @DisplayName("Should return hardcoded value in case of error (with cancelling Flux) ")
    @Test
    void shouldReturnHardcodedValueInCaseOfError() {
        callFirstSignalSource().log().map(it -> 10 / (5 - it)).onErrorReturn(-1).subscribe(DefaultSimpleSubscriber.create());
    }

    @DisplayName("Should emit signal from alternativesource in case of error (with cancelling Flux)")
    @Test
    void shouldEmitSignalFromAlternativeSourceInCaseOfError() {
        callFirstSignalSource().log()
                               .map(it -> 10 / (5 - it))
                               .onErrorResume(e -> callAlternativeSignalSource())
                               .subscribe(DefaultSimpleSubscriber.create());
    }

    @DisplayName("Should continue Flux when error occured")
    @Test
    void shouldContinueFluxWhenErrorOccured() {
        callFirstSignalSource().log().map(it -> 10 / (5 - it)).onErrorContinue((thrownException, objectCausedTheError) -> {
            //simply skip
            log.info(String.format("Error occured => simply skip action => value=%s",objectCausedTheError));
        }).subscribe(DefaultSimpleSubscriber.create());
    }

    /**
     * In real-life scenario it could be first microservice call
     */
    private Flux<Integer> callFirstSignalSource() {
        return Flux.range(1, 10);
    }

    /**
     * In real-life scenario it could be alternative microservice call - maybe another node
     */
    private Flux<Integer> callAlternativeSignalSource() {
        return Flux.create(sink -> {
                               for (int i = 0; i < 10; i++) {
                                   var myvalue = faker.random().nextInt(100, 150);
                                   sink.next(myvalue);
                               }
                               sink.complete();
                           }

                          );
    }
}

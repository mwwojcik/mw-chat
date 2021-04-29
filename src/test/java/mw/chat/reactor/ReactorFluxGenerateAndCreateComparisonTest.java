package mw.chat.reactor;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class ReactorFluxGenerateAndCreateComparisonTest {

    private Faker faker = new Faker();

    @DisplayName("Should emit max 10 flux signals via create method")
    @Test
    void shouldEmitFluxSignalsViaCreateMethod() {
        //given
        //max=10
        //until country!=canada
        //react on take() operator - cancelling flux
        Flux.create(fluxSink -> {
            String countryName = "";
            int counter = 0;
            /*
            This code will be executed only one time. There is only one instance fluxSink.
            if you want to emit multiple signals the loop must be run inside this block.
             */
            do {
                countryName = faker.country().name();
                fluxSink.next(countryName);
                System.out.println("emitting=>" + countryName);
                counter++;
            } while (!countryName.equalsIgnoreCase("canada") && counter < 10 && !fluxSink.isCancelled());
            fluxSink.complete();

        }).take(3).subscribe(DefaultSimpleSubscriber.create());
        /*
         With while loop conditions : !countryName.equalsIgnoreCase("canada") && counter < 10
         subscriber received 3 signals, but producer emitted 10 singnals!

         After adding another condition => !fluxSink.isCancelled() - producer emitted only 3 items!
        * */
    }

    @DisplayName("Should emit max number of signals via generate method without state")
    @Test
    void shouldEmitMaxNumberOfSignalsViaGenerateMethodWithoutState() {

        //given
        //max=10
        //until country!=canada
        //react on take() operator - cancelling flux
        //user cancel - exit

        /*
         In this simple version of Flux max=10 condition couldn't be realized.

        * */

        Flux.generate(synchronousSink -> {
            /*
            This block of code is executed MANY times. It can emit only one signal at once.
            * */

            String countryName = faker.country().name();
            System.out.println("Emitting=>" + countryName);
            synchronousSink.next(countryName);
            if (countryName.equalsIgnoreCase("canada")) {
                synchronousSink.complete();
            }
        })
            /* The loop is managed outside the genrator, therefore the take operator is respected. */
            //.take(3)
            .subscribe(DefaultSimpleSubscriber.create());
    }

    @DisplayName("Should emit max number of signals via generate method with state")
    @Test
    void shouldEmitMaxNumberOfSignalsViaGenerateMethodWithState() {

        //given
        //max=10
        //until country!=canada
        //react on take() operator - cancelling flux
        //user cancel - exit

        /*
         In this simple version of Flux max=10 condition couldn't be realized.

        * */

        Flux.generate(() -> 1, //initial state
                      (counter, synchronousSink) -> {
                 /*
            This block of code is executed MANY times. It can emit only one signal at once.
            * */

                          String countryName = faker.country().name();
                          System.out.println("Emitting=>" + countryName);
                          synchronousSink.next(countryName);
                          if (countryName.equalsIgnoreCase("canada")) {
                              synchronousSink.complete();
                          }
                          return counter++;
                      })
            /* The loop is managed outside the genrator, therefore the take operator is respected. */
            //.take(3)
            .subscribe(DefaultSimpleSubscriber.create());
    }
}
/*
{

* */
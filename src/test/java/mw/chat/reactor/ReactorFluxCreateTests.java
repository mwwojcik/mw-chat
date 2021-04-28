package mw.chat.reactor;

import com.github.javafaker.Faker;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

@Slf4j
public class ReactorFluxCreateTests {

    Faker faker = new Faker();

    @DisplayName("Should emit simple flux signal with country names")
    @Test
    void shouldEmitSimpleFluxWithCountryNames() {

        Flux.create(fluxSink -> {
            IntStream.range(0, 100).forEach(it -> {
                var country = faker.country().name();
                fluxSink.next(country);
            });
            fluxSink.complete();
        }).subscribe(DefaultSimpleSubscriber.create());
    }

    @DisplayName("Should emit flux signals based on custom flux sink consumer")
    @Test
    void shouldEmitFluxSignalsBasedOnCustomFluxSinkConsumer() {

        var emitter = CountryNamesFluxSinkConsumer.create();

        //subsciption only
        Flux.create(emitter).subscribe(DefaultSimpleSubscriber.create());

        //Signal will be emitted only by manual produce it
        IntStream.range(0, 10).forEach(it -> emitter.produce());
    }

    @DisplayName("Should emit max 10 flux signals")
    @Test
    void shouldEmitFluxSignals() {

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
                System.out.println("emitting=>"+countryName);
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

}

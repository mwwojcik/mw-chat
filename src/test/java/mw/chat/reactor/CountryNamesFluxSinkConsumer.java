package mw.chat.reactor;

import com.github.javafaker.Faker;
import java.util.function.Consumer;
import reactor.core.publisher.FluxSink;

public class CountryNamesFluxSinkConsumer implements Consumer<FluxSink<String>> {

    private Faker faker = new Faker();
    private FluxSink<String> stringFluxSink;

    @Override
    public void accept(FluxSink<String> stringFluxSink) {
        this.stringFluxSink = stringFluxSink;
    }

    public void produce() {
        stringFluxSink.next(faker.country().name());

    }

    @Override
    public Consumer<FluxSink<String>> andThen(Consumer<? super FluxSink<String>> after) {
        return null;
    }

    public static CountryNamesFluxSinkConsumer create() {
        return new CountryNamesFluxSinkConsumer();
    }


}

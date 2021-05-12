package mw.chat.reactor.coldhot;

import java.time.Duration;
import java.util.stream.Stream;
import mw.chat.reactor.DefaultSimpleSubscriber;
import mw.chat.reactor.Sleeper;
import org.assertj.core.api.Fail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

/*
    https://www.vinsguru.com/reactor-hot-publisher-vs-cold-publisher/

    Publishers by default do not produce any value unless at least 1 observer subscribes to it. Publishers create new data producers for each new subscription.

 * Cold publisher emits the same set of signals to every subscriber.
 *
 * It is a netflix case. Each subscriber gets his own stream.
 * */
public class ColdPublisherTest {

    @DisplayName("Should emit cold stream of movie scenes")
    @Test
    void shouldEmitColdStreamOfMovieScenes() {

        var movieColdStream = Flux.fromStream(()->movieScenes()).delayElements(Duration.ofSeconds(2));

        movieColdStream.subscribe(DefaultSimpleSubscriber.create("Sam"));
        Sleeper.sleepSecconds(5);
        movieColdStream.subscribe(DefaultSimpleSubscriber.create("Mike"));
        Sleeper.sleepSecconds(60);
     }

    public Stream<String> movieScenes() {
     return Stream.of("Scene 1","Scene 2","Scene 3","Scene 4","Scene 5","Scene 6","Scene 7") ;
     }

}

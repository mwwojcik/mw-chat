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
 * https://www.vinsguru.com/reactor-hot-publisher-vs-cold-publisher/
 *
 * Hot Publishers do not create new data producer for each new subscription (as the Cold Publisher does).
 * Instead there will be only one data producer and all the observers listen to the data produced by the single data producer.
 * So all the observers get the same data.
 *
 * Hot publisher emits signals continuously.
 *
 * It is a TV case. Signals will be emitted regardless of subscribers. If the subscriber connects late,
 * he will not receive earlier signals.
 * */
public class HotPublisherTest {


    @DisplayName("Should emit hot stream of movie scenes by using share")
    @Test
    void shouldEmitHotStreamOfMovieScenesByUsingShare() {

        //share = publish().refCount(1) - alias method

        var movieHotStream = Flux.fromStream(() -> movieScenes()).delayElements(Duration.ofSeconds(2)).share();

        movieHotStream.subscribe(DefaultSimpleSubscriber.create("Sam"));
        Sleeper.sleepSecconds(5);
        movieHotStream.subscribe(DefaultSimpleSubscriber.create("Mike"));
        Sleeper.sleepSecconds(60);
    }

    @DisplayName("Should emit hot stream of movie scenes by using publish with refcount")
    @Test
    void shouldEmitHotStreamOfMovieScenesByUsingPublishWithRefcount() {

        //refCount() - number of subscribers needed to start emit  signals

        var movieHotStream = Flux.fromStream(() -> movieScenes()).delayElements(Duration.ofSeconds(2)).publish().refCount(2);

        movieHotStream.subscribe(DefaultSimpleSubscriber.create("Sam"));
        Sleeper.sleepSecconds(5);
        movieHotStream.subscribe(DefaultSimpleSubscriber.create("Mike"));
        Sleeper.sleepSecconds(60);
    }


    /*
    * But here when the second subscriber joins, 
    * the source has already emitted data & completed.
    * So the second subscription repeats the emission process.
    * */
    @DisplayName("Should emit all signals when the second subscriber connects after completing the first emission")
    @Test
    void shouldEmitAllSignalsWhenTheSecondSubscriberConnectsAfterCompletingTheFirstEmission() {

        final int LONG_PAUSE_BEFORE_CONNECTING_THE_SECOND_SUBSCIBER = 10;

        //refCount() - number of subscribers needed to start emit  signals
        // 7 scenes with 1 second pause ~ 8s
        var movieHotStream = Flux.fromStream(() -> movieScenes()).delayElements(Duration.ofSeconds(1)).publish().refCount(1);

        movieHotStream.subscribe(DefaultSimpleSubscriber.create("Sam"));
        Sleeper.sleepSecconds(LONG_PAUSE_BEFORE_CONNECTING_THE_SECOND_SUBSCIBER);
        movieHotStream.subscribe(DefaultSimpleSubscriber.create("Mike"));
        Sleeper.sleepSecconds(30);
        //EFFECT Sam and Mike received all scenes
    }

    public Stream<String> movieScenes() {
        return Stream.of("Scene 1", "Scene 2", "Scene 3", "Scene 4", "Scene 5", "Scene 6", "Scene 7");
    }

    @DisplayName("Should emit only some signals when the second subsciber connects before completing the first emission")
    @Test
    void shouldEmitOnlySomeSignalsWhenTheSecondSubsciberConnectsBeforeCompletingTheFirstEmission() {
        final int SHORT_PAUSE_BEFORE_CONNECTING_THE_SECOND_SUBSCIBER = 10;

        //refCount() - number of subscribers needed to start emit  signals
        // 7 scenes with 2 second pause ~ 16s
        var movieHotStream = Flux.fromStream(() -> movieScenes()).delayElements(Duration.ofSeconds(2)).publish().refCount(1);

        movieHotStream.subscribe(DefaultSimpleSubscriber.create("Sam"));
        Sleeper.sleepSecconds(SHORT_PAUSE_BEFORE_CONNECTING_THE_SECOND_SUBSCIBER);
        movieHotStream.subscribe(DefaultSimpleSubscriber.create("Mike"));
        Sleeper.sleepSecconds(30);
        //EFFECT: Sam received all scenes, but Mike received only 5,6 and 7 scene
     }

     @DisplayName("Should emit signals without connecting any subscribers")
     @Test
     void shouldEmitSignalsWithoutConnectingAnySubscribers() {
        var movieHotStream = Flux.fromStream(() -> movieScenes()).delayElements(Duration.ofSeconds(1)).publish().autoConnect(0);
         Sleeper.sleepSecconds(3);
         movieHotStream.subscribe(DefaultSimpleSubscriber.create("Sam"));
         Sleeper.sleepSecconds(10);
         System.out.println("Mike is about to join ");
         movieHotStream.subscribe(DefaultSimpleSubscriber.create("Mike"));
         Sleeper.sleepSecconds(30);
         //EFFECT: Sam received movie from Scene 3 (emission has started without subscription) but Mike can't watch (signal
         // source is empty.
      }

      @DisplayName("Should cache elements from first stream and emit it immediately")
      @Test
      void shouldCacheElementsFromFirstStreamAndEmitItImmediately() {
          //cache() = cache(Int.MAX)
          //cache()= publish().reply() - it's alias method
          var movieHotStream = Flux.fromStream(() -> movieScenes()).delayElements(Duration.ofSeconds(1)).cache();
          Sleeper.sleepSecconds(3);
          movieHotStream.subscribe(DefaultSimpleSubscriber.create("Sam"));
          Sleeper.sleepSecconds(10);
          System.out.println("Mike is about to join ");
          movieHotStream.subscribe(DefaultSimpleSubscriber.create("Mike"));
          Sleeper.sleepSecconds(30);
          //EFFECT: Mike gets his scenes immediately. It will be cached during first emission.
       }
}

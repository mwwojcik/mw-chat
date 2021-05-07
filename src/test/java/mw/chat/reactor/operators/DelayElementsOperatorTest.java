package mw.chat.reactor.operators;

import java.time.Duration;
import mw.chat.reactor.DefaultSimpleSubscriber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class DelayElementsOperatorTest {

    /*
    * Delay each of this Flux elements (Subscriber.onNext(T) signals) by a given Duration.
    * Warrning!
    * Producer works in a different thread than the subscriber .
    * The sequence is as follows:
    * 09:01:08.491 [Test worker] INFO reactor.Flux.Range.1 - | onSubscribe([Synchronous Fuseable] FluxRange.RangeSubscription)
      09:01:08.495 [Test worker] INFO reactor.Flux.Range.1 - | request(32)
      * EMIT DEFAULT 32 SIGNALS
      * RECEIVE ALL 32 SIGNALS in different Thread (with delay)
      * EMIT NEXT 32 SIGNALS
      * RECEIVE EVENTS in different Thread (with delay)
      *
      * The main thread must be stopped to be able to take effect!!!!
      * Thread.sleep(60000);
      * Otherwise, only the emission of the first portion of the signal can be observed!
    * */
    @DisplayName("Should present delay elements operator")
    @Test
    void shouldPresentDelayElementsOperator()
        throws InterruptedException {

     //Flux.range(1,100).log().delayElements(Duration.ofMillis(500)).subscribe(DefaultSimpleSubscriber.create());
     //WARRNING!! Different log() position - completelly different result!
     Flux.range(1,100).delayElements(Duration.ofMillis(500)).log().subscribe(DefaultSimpleSubscriber.create());
     Thread.sleep(60000);
    }
}


/*
Cześć,chciałbym poprosić Was o pomoc w zrozumieniu działania operatora Flux.delayElements(). Trochę pogubiłem się w tym co zadziewa się tam na wątkach, ale po kolei.Test wygląda tak:   @DisplayName("Should present delay elements operator")
    @Test
    void shouldPresentDelayElementsOperator()
        throws InterruptedException {
        Flux.range(1, 100).log().delayElements(Duration.ofSeconds(1)).subscribe(DefaultSimpleSubscriber.create());
    }

Wynik jest zaskakujący bo zaobserwować dało się tylko emisję partii 32 (request(32)) sygnałów bez jakiegokolwiek opóźnienia.09:39:29.944 [Test worker] INFO reactor.Flux.Range.1 - | onSubscribe([Synchronous Fuseable] FluxRange.RangeSubscription)
09:39:29.948 [Test worker] INFO reactor.Flux.Range.1 - | request(32)
09:39:29.949 [Test worker] INFO reactor.Flux.Range.1 - | onNext(1)
09:39:29.977 [Test worker] INFO reactor.Flux.Range.1 - | onNext(2)
..................................................................
09:39:29.980 [Test worker] INFO reactor.Flux.Range.1 - | onNext(32)
BUILD SUCCESSFUL in 2sPo wstrzymaniu głównego wątku 	Thread.sleep(60000) wygląda to tak:09:46:11.510 [Test worker] DEBUG reactor.util.Loggers - Using Slf4j logging framework
09:46:11.531 [Test worker] INFO reactor.Flux.Range.1 - | onSubscribe([Synchronous Fuseable] FluxRange.RangeSubscription)
09:46:11.535 [Test worker] INFO reactor.Flux.Range.1 - | request(32)
09:46:11.536 [Test worker] INFO reactor.Flux.Range.1 - | onNext(1)
09:46:11.562 [Test worker] INFO reactor.Flux.Range.1 - | onNext(2)
09:46:11.564 [Test worker] INFO reactor.Flux.Range.1 - | onNext(3)
..................................................................
09:46:11.567 [Test worker] INFO reactor.Flux.Range.1 - | onNext(32)
parallel-1 => => DefaultSimpleSubscriber Received : 1
parallel-2 => => DefaultSimpleSubscriber Received : 2
..................................................................
parallel-11 => => DefaultSimpleSubscriber Received : 23
09:46:13.040 [parallel-11] INFO reactor.Flux.Range.1 - | request(24)
09:46:13.041 [parallel-11] INFO reactor.Flux.Range.1 - | onNext(33)
09:46:13.041 [parallel-11] INFO reactor.Flux.Range.1 - | onNext(34)
..................................................................
09:46:13.046 [parallel-11] INFO reactor.Flux.Range.1 - | onNext(56)
parallel-12 => => DefaultSimpleSubscriber Received : 24
parallel-1 => => DefaultSimpleSubscriber Received : 25
..................................................................
parallel-11 => => DefaultSimpleSubscriber Received : 47
09:46:14.581 [parallel-11] INFO reactor.Flux.Range.1 - | request(24)
09:46:14.581 [parallel-11] INFO reactor.Flux.Range.1 - | onNext(57)
09:46:14.581 [parallel-11] INFO reactor.Flux.Range.1 - | onNext(58)
.................................................................
parallel-4 => => DefaultSimpleSubscriber CompletedPytania są następujące:
1. Co tu w ogóle się zadziało z wątkami ?
2. Dlaczego pierwszy request jest na 32 sygnały a reszta już na 24?
3. Dlaczego pierwsza emisja jest w watku Test Worker (wątek główny) a następne już przez wątki z jakiejś dziwnej puli?
4. Czy Producent i Subskrybent pracują na tej samej puli i wątek parallel-11 pojawiający się przy onNext() i received() to ten sam wątek ? Czy też tak samo nazywający się ale z pochodzący z innych pul ?
5. Dokumentacja mówi że operator działa tak:
"Delay each of this Flux elements (Subscriber.onNext(T) signals) by a given Duration."
ale patrząc po logach to nie wydaje mi się żeby onNext() był opóźniany. Wygląda to tak jakby onNext był wywoływany bez opóźnienia, a jego wynik był buforowany i puszczany - już z opóźnieniem na inny wątek.
3 replies
Tomasz Nurkiewicz  12 hours ago
OK, poruszasz tematy co najmniej na kwadrans tłumaczenie, więc postaram się krótko:

    1. Co tu w ogóle się zadziało z wątkami ?

Operator log() jest przed delayElements(), dlatego pracuje na puli Test Worker. Potem pojawia się pula parallel() bo na tej puli następuje emisja z operator delayElements(). Mooocno upraszczam.

    2. Dlaczego pierwszy request jest na 32 sygnały a reszta już na 24?

Tak działa backpressure. Najpierw zapełnia bufor maksymalnie, a gdy jego rozmiar spadnie do 25%, dobiera.

    3. Dlaczego pierwsza emisja jest w watku Test Worker (wątek główny) a następne już przez wątki z jakiejś dziwnej puli?

Ta dziwna pula jest domyślna w delayElements(). Możesz ją zmienić tak: .delayElements(Duration.ofSeconds(1), Schedulers.boundedElastic()). Natomiast na początku emisja jest w Test Worker (wątek główny), bo domyślnie Reactor używa wątku klienta (testy jednostkowego). Skąd to nagłe przełączenie to dużo dłuższy temat.

     4. Czy Producent i Subskrybent pracują na tej samej puli i wątek parallel-11 pojawiający się przy onNext() i received() to ten sam wątek ? Czy też tak samo nazywający się ale z pochodzący z innych pul ?

Producent i subskrybent domyślnie pracują na tej samej puli, a nawet wątku, chyba, że w międzyczasie zrobiłeś subscribeOn, publishOn, delay*, flatMap i wiele, wiele innych operatorów, które przełączają scheduler. Także w praktyce - prawie zawsze działają na innych wątkach. To jest też dużo głębszy temat.

    5. [...] nie wydaje mi się żeby onNext() był opóźniany. Wygląda to tak jakby onNext był wywoływany bez opóźnienia, a jego wynik był buforowany i puszczany - już z opóźnieniem na inny wątek.

Przesuń log() poniżej delayElements(). (edited)
Mariusz Wójcik  11 hours ago
@nurkiewicz - dziękuję :slightly_smiling_face: - teraz to wygląda zupełnie inaczej
* */
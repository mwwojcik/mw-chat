package mw.chat.reactor.operators;

import mw.chat.reactor.DefaultSimpleSubscriber;
import org.assertj.core.api.Fail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class LimitRateOperatorTest {

    @DisplayName("Should presents limit rate operator - back pressure")
    @Test
    void shouldPresentsLimitRateOperatorBackPressure() {
        /*
        * It asks for signals to be transmitted in batches(portions). A single portion is 75% of the limitRate value.
        * limitRate=100
        * Emit 75 (75% of 100) signals, request next 75 items
        * */
        Flux.range(0,1000).log().limitRate(100).subscribe(DefaultSimpleSubscriber.create());
     }
}

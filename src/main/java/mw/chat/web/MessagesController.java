package mw.chat.web;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@RestController
@RequestMapping("/api/messages")
public class MessagesController {

    private ReactiveMessageRepository repo = new ReactiveMessageRepository(Executors.newFixedThreadPool(5));

    @GetMapping(path = "/server-messages-events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamFlux() {
        repo.add("");
        return Flux.create(repo);
    }

    @PostMapping
    public ResponseEntity addMessage(@RequestBody String message) {
        repo.add(message);
        return ResponseEntity.ok().build();
    }

    @PostConstruct
    public void addInitially( ) {
        for (int i = 0; i < 1000000; i++) {
            repo.add(UUID.randomUUID().toString());
        }
    }


}

class ReactiveMessageRepository implements Consumer<FluxSink<String>> {

    private BlockingQueue<String> messages = new LinkedBlockingQueue<>(100);
    private Executor executor;

    public ReactiveMessageRepository(Executor executor) {
        this.executor = executor;
    }

    public void add(String message) {
        messages.offer(message);
    }

    @Override
    public void accept(FluxSink<String> fluxSink) {
        executor.execute(() -> {
            try {
                while (true) {
                        Thread.sleep(2000);
                        fluxSink.next(messages.take());
                }
            } catch (InterruptedException ex) {
                throw new IllegalStateException(ex);
            }
        });
    }

    @Override
    public Consumer<FluxSink<String>> andThen(Consumer<? super FluxSink<String>> after) {
        return null;
    }
}

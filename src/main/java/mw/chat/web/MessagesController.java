package mw.chat.web;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;
import org.reactivestreams.Publisher;
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

    private ReactiveMessageRepositoryFluxSink repo = new ReactiveMessageRepositoryFluxSink();
    private ReactiveMessageRepository repoWithHistory=new ReactiveMessageRepository();

    @GetMapping(path = "/server-messages-events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Publisher<String> streamFlux() {
        return Flux.create(repo);
    }

    @GetMapping(path = "/server-messages-events-history", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Publisher<String> streamFluxWithHistory() {
        return repoWithHistory.receiveMessages();
    }

    @PostMapping
    public ResponseEntity addMessage(@RequestBody String message) {
        repo.add(message);
        return ResponseEntity.ok().build();
    }
}

class ReactiveMessageRepository {

    private ConcurrentLinkedQueue<Consumer<String>> messages2 = new ConcurrentLinkedQueue<>();
    private Set<String> history = new ConcurrentSkipListSet<>();

    public void add(String message) {
        history.add(message);
        messages2.forEach(consumer -> consumer.accept(message));
    }

    public Publisher<String> receiveMessages() {
        return Flux.fromIterable(history).concatWith(Flux.create(fluxSink -> messages2.add(fluxSink::next)));
    }
}

class ReactiveMessageRepositoryFluxSink implements Consumer<FluxSink<String>> {

    private ConcurrentLinkedQueue<Consumer<String>> messages2 = new ConcurrentLinkedQueue<>();

    public void add(String message) {
        messages2.forEach(consumer -> consumer.accept(message));
    }

    @Override
    public void accept(FluxSink<String> fluxSink) {
        messages2.add(fluxSink::next);
    }

    @Override
    public Consumer<FluxSink<String>> andThen(Consumer<? super FluxSink<String>> after) {
        return null;
    }
}
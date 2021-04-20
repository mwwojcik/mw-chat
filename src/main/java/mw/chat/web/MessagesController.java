package mw.chat.web;

import java.time.Duration;
import java.time.LocalTime;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import mw.chat.service.MessagesTopicProcessor;
import org.springframework.beans.factory.annotation.Autowired;
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

    private ReactiveMessageRepository repo=new ReactiveMessageRepository(Executors.newFixedThreadPool(10));


    @GetMapping(path = "/server-messages-events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamFlux() {
        repo.add("");
        return Flux.create(repo);
    }

    @PostMapping
    public ResponseEntity addMessage(@RequestBody String message) {
        repo.add(message);
     return ResponseEntity.ok().build() ;
     }

}

class ReactiveMessageRepository implements Consumer<FluxSink<String>> {

    private BlockingQueue<String> messages = new LinkedBlockingQueue<>(100);
    private Executor executor;

    public ReactiveMessageRepository(Executor executor) {
        this.executor = executor;
    }

    public void add(String message){
        messages.offer(message);
    }

    @Override
    public void accept(FluxSink<String> fluxSink) {
        executor.execute(()->{
            try {
                while (true){
                    fluxSink.next(messages.take());
                }
            }catch (InterruptedException ex){
                throw new IllegalStateException(ex);
            }
        });
    }

    @Override
    public Consumer<FluxSink<String>> andThen(Consumer<? super FluxSink<String>> after) {
        return null;
    }
}
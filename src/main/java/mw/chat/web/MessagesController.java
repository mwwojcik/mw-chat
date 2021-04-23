package mw.chat.web;

import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

@RestController
@RequestMapping("/api/messages")
public class MessagesController {

	private ReactiveMessageRepository repo = new ReactiveMessageRepository();

	@GetMapping(path = "/server-messages-events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Publisher<String> streamFlux() {
		repo.add("");
		return repo.receiveMessages();
	}

	@PostMapping
	public ResponseEntity addMessage(@RequestBody String message) {
		repo.add(message);
		return ResponseEntity.ok().build();
	}
}

class ReactiveMessageRepository {

	private ConcurrentLinkedQueue<Consumer<String>> messages2 = new ConcurrentLinkedQueue<>();
	private Set<String> history = new HashSet<>();

	public void add(String message) {
		history.add(message);
		messages2.forEach(consumer -> consumer.accept(message));
	}

	public Publisher<String> receiveMessages() {
		return Flux.fromIterable(history)
				   .concatWith(Flux.create(fluxSink -> messages2.add(fluxSink::next)));
	}
}

package mw.chat.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mw.chat.service.MessagesTopicProcessor;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Slf4j
public class MessagesTopicHandler implements WebSocketHandler {

    public MessagesTopicHandler(MessagesTopicProcessor processor) {

        this.processor = processor;

    }

    private final Sinks.Many<String> broadcast = Sinks.many().replay().limit(5);

    private MessagesTopicProcessor processor;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        session
            .receive()
            .doOnSubscribe(s -> log.info("Got new subscriber, total: {}", broadcast.currentSubscriberCount() + 1))
            .map(WebSocketMessage::getPayloadAsText)
            .doOnNext(incoming -> log.info("Got message \"{}\", broadcasting to all {} subscribers", incoming, broadcast.currentSubscriberCount()))
            .doOnNext(broadcast::tryEmitNext)
            .doOnTerminate(() -> log.info("Subscriber disconnected, total: {}", broadcast.currentSubscriberCount()))
            .subscribe();
        return session.send(broadcast.asFlux().map(session::textMessage));
    }
}

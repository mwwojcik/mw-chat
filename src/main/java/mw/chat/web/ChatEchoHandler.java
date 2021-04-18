package mw.chat.web;

import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ChatEchoHandler implements WebSocketHandler {
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Flux<WebSocketMessage> messageFlux = session.receive().map(message -> {
            String payload = message.getPayloadAsText();
            return "Received: " + payload;
        }).map(session::textMessage);
        return session.send(messageFlux);
    }
}

/*
* usage:  wscat -c ws://localhost:8080/ws/echo
* */
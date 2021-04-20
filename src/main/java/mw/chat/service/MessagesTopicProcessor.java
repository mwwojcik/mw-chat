package mw.chat.service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

public class MessagesTopicProcessor {

    private List<String> messages = new CopyOnWriteArrayList<>();
    private List<WebSocketSession> channels = new CopyOnWriteArrayList<>();

    public void publish(String message) {
        messages.add(message);
        topic(message);
    }

    private void topic(String message) {
        for (var session : channels) {
            var webSocketMessage = session.textMessage(message);
            session.send(Mono.just(webSocketMessage));
        }
    }

    public Mono<Void> register(WebSocketSession session) {
        channels.add(session);
        var webSocketMessage = session.textMessage("User registered!");
        return session.send(Mono.just(webSocketMessage));
    }
}

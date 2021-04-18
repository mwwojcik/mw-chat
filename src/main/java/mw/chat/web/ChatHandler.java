package mw.chat.web;

import lombok.AllArgsConstructor;
import mw.chat.model.UserName;
import mw.chat.service.MultiUserChatProcessor;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class ChatHandler implements WebSocketHandler {

    private MultiUserChatProcessor chatProcessor;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        var user = findUserName(session);
        return chatProcessor.register(user, session);
    }

    private UserName findUserName(WebSocketSession session) {
        try {
            return UserName.from(session.getHandshakeInfo().getUri().getQuery().split("=")[1]);

        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

}

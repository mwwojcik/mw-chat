package mw.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import mw.chat.model.ChatMessage;
import mw.chat.model.UserName;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

public class MultiUserChatProcessor {

    ConcurrentMap<UserName, WebSocketSession> users = new ConcurrentHashMap<>();
    ObjectMapper objectMapper = new ObjectMapper();

    public Mono<Void> register(UserName user, WebSocketSession session) {
        users.put(user, session);
        return session.receive()
                      .flatMap(webSocketMessage -> deliver(session, webSocketMessage))
                      .then()
                      .doFinally(signal -> users.remove(user));
    }


    private Mono<Void> deliver(WebSocketSession session, WebSocketMessage webSocketMessage) {
        String payload = webSocketMessage.getPayloadAsText();
        ChatMessage message;
        try {
            message = objectMapper.readValue(payload, ChatMessage.class);
            UserName targetId = targetUser(message);
            if (userIsOnline(targetId)) {
                WebSocketSession targetSession = users.get(targetId);
                    WebSocketMessage textMessage = targetSession.textMessage(message.getMessage());
                    return targetSession.send(Mono.just(textMessage));
            }
        } catch (JsonProcessingException e) {
            return session.send(Mono.just(session.textMessage(e.getMessage())));
        }
        return session.send(Mono.just(session.textMessage("target user is not online")));
    }

    private UserName targetUser(ChatMessage message) {
        return UserName.from(message.getUser());
    }

    private boolean userIsOnline(UserName targetId) {
        return users.containsKey(targetId);
    }
}
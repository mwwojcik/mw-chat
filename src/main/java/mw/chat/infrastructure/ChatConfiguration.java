package mw.chat.infrastructure;

import java.util.LinkedHashMap;
import java.util.Map;
import mw.chat.web.ChatEchoHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

public class ChatConfiguration {
    @Bean
    public HandlerMapping webSocketMapping() {
        SimpleUrlHandlerMapping simpleUrlHandlerMapping = new SimpleUrlHandlerMapping();
        Map<String, WebSocketHandler> handlerMap = new LinkedHashMap<>();

        handlerMap.put("/ws/echo", new ChatEchoHandler());

        simpleUrlHandlerMapping.setUrlMap(handlerMap);
        simpleUrlHandlerMapping.setOrder(-1);
        return simpleUrlHandlerMapping;
    }
}

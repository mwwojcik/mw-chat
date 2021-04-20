package mw.chat.infrastructure;

import java.util.LinkedHashMap;
import java.util.Map;
import mw.chat.service.MultiUserChatProcessor;
import mw.chat.service.MessagesTopicProcessor;
import mw.chat.web.ChatEchoHandler;
import mw.chat.web.ChatHandler;
import mw.chat.web.MessagesTopicHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

public class ChatConfiguration {

    @Bean
    public HandlerMapping webSocketMapping(ChatHandler chatHandler,
                                           MessagesTopicHandler messagesTopicHandler) {
        SimpleUrlHandlerMapping simpleUrlHandlerMapping = new SimpleUrlHandlerMapping();
        Map<String, WebSocketHandler> handlerMap = new LinkedHashMap<>();

        handlerMap.put("/ws/echo", new ChatEchoHandler());
        handlerMap.put("/ws/chat", chatHandler);
        handlerMap.put("/ws/messages", messagesTopicHandler);

        simpleUrlHandlerMapping.setUrlMap(handlerMap);
        simpleUrlHandlerMapping.setOrder(-1);
        return simpleUrlHandlerMapping;
    }

    @Bean
    public ChatHandler chatHandler(MultiUserChatProcessor chatProcessor) {
        return new ChatHandler(chatProcessor);
    }

    @Bean
    public MultiUserChatProcessor multiUserChatProcessor() {
        return new MultiUserChatProcessor();
    }

    @Bean
    public MessagesTopicHandler reactiveCollectionHandler(MessagesTopicProcessor processor) {
        return new MessagesTopicHandler(processor);
    }

    @Bean
    public MessagesTopicProcessor reactiveCollectionOfMessages() {
     return new MessagesTopicProcessor() ;
     }



}

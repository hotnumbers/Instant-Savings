package com.example.tianxingwang.instasavings.client.stomp;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AbstractService;

import org.eclipse.jetty.websocket.jsr356.ClientContainer;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Consumer;

import importio.client.ImportIoClient;
import importio.client.data.QueryByExtractorRequest;
import importio.client.data.QueryMessage;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.Synchronized;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FieldDefaults(level=AccessLevel.PRIVATE)
public abstract class ImportIoStompClient extends AbstractService implements ImportIoClient {

    ImmutableSet<Consumer<QueryMessage>> consumers = ImmutableSet.of();
    
    @Setter
    String url = "wss://api.import.io/client";
    
    @Setter
    String apiKey = 
            "933ee4c41f80432a8f2b01a35af3b6f689a7e725911ab341336828fb7f05cab8b28471ec4fe6c4d42c108b418ed5ebd8d40a7da185bbe255bca7ff841536d28d169eee6793ac263604e7598c391b5e4b";
    
    StandardWebSocketClient client;
    WebSocketStompClient stompClient;
    StompSessionHandler sessionHandler;
    StompSession stompSession;

    ClientContainer wsContainer;

    @Override
    @SneakyThrows
    protected void doStart() {
        wsContainer = new ClientContainer();
        try {
            wsContainer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        client = new StandardWebSocketClient(wsContainer);
        stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        sessionHandler = new StompSessionHandler();
        WebSocketHttpHeaders headers = getHandshakeHeaders();
        stompClient.connect(url, headers, sessionHandler).addCallback(new ListenableFutureCallback<StompSession>() {


            @Override
            public void onSuccess(StompSession result) {
                stompSession = result;
                stompSession.subscribe("/user/queue/messages", new StompFrameHandler() {
                    
                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        System.out.println ("Got frame: {} {}" + headers + payload);
                        QueryMessage message = (QueryMessage)payload;
                        consumers.forEach( c -> c.accept(message) );
                    }
                    
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return QueryMessage.class;
                    }
                });
                notifyStarted();
            }

            @Override
            public void onFailure(Throwable ex) {
                notifyFailed(ex);
            }
        });
    }

    /* (non-Javadoc)
     * @see importio.client.ImportIoClient#queryByExtractor(importio.client.data.QueryByExtractorRequest)
     */
    @Override
    public void queryByExtractor(QueryByExtractorRequest input) {
        stompSession.send("/app/query/extractor", input);
    }

    private WebSocketHttpHeaders getHandshakeHeaders() {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        if ( apiKey == null ) {
            Map<String, String> env = System.getenv();
            String user = env.get("io-user");
            Preconditions.checkNotNull(user, "No API key supplied");
            headers.set("io-user", user);
            headers.set("io-subscription", env.get("io-subscription"));
        } else {
            headers.set("x-api-key", apiKey);
        }
        return headers;
    }
    
    @Override
    @SneakyThrows
    protected void doStop() {
        stompSession.disconnect();
        stompClient.stop();
        try {
            wsContainer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifyStopped();
    }
    
    public class StompSessionHandler extends StompSessionHandlerAdapter {

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            System.out.println("New session established {}" + session.getSessionId());
        }

        @Override
        public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
            // TODO: handle exception
            System.out.println("Got an exception"+ exception);
        }

        @Override
        public void handleTransportError(StompSession session, Throwable exception) {
            // TODO: handle transport error
            System.out.println("Transport error" + exception);
        }
        
    }
    
    @Override
    @Synchronized
    public void addMessageListener(Consumer<QueryMessage> consumer) {
        consumers = ImmutableSet.<Consumer<QueryMessage>>builder().addAll(consumers).add(consumer).build();
    }

    @Override
    @Synchronized
    public void removeMessageListener(Consumer<QueryMessage> consumer) {
        consumers = ImmutableSet.copyOf(Sets.difference(consumers, ImmutableSet.of(consumer))); 
    }
}

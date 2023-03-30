package client.utils;

import com.google.inject.Inject;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSession.Subscription;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class WebsocketUtils {
    private final StompSession session;
    private final Map<String, Subscription> subscriptions=new ConcurrentHashMap<>();

    @Inject
    public WebsocketUtils(ServerUtils serverUtils) {
        this.session=connect("ws://"+serverUtils.getAddress()+"/websocket");
    }

    private StompSession connect(String url){
        var client=new StandardWebSocketClient();
        var stomp=new WebSocketStompClient(client);
        stomp.setMessageConverter(new MappingJackson2MessageConverter());
        try{
            return stomp.connect(url, new StompSessionHandlerAdapter(){}).get();
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
            System.out.println("couldn't connect to websockets...");
        }catch (ExecutionException | RuntimeException e){
            System.out.println("couldn't connect to websockets...");
        }
        return null;
    }

    @SuppressWarnings("all")
    public <T> void registerForMessages(String destination, Class<T> type, Consumer<T> consumer){
        if(session==null)
            return;
        subscriptions.put(destination,session.subscribe(destination, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return type;
            }
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                consumer.accept((T) payload);
            }
        }));
    }

    public void unsubscribeFromMessages(String destination){
        if(subscriptions.get(destination)!=null)
            subscriptions.get(destination).unsubscribe();
    }
}

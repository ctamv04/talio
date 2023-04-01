package config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import server.configs.WebsocketConfig;

import static org.mockito.Mockito.*;

public class WebsocketConfigTest {
    @InjectMocks
    private WebsocketConfig sut;
    @Mock
    private StompEndpointRegistry stompEndpointRegistryMock;
    @Mock
    private MessageBrokerRegistry messageBrokerRegistryMock;

    @BeforeEach
    public void setUp(){
        stompEndpointRegistryMock=mock(StompEndpointRegistry.class);
        messageBrokerRegistryMock=mock(MessageBrokerRegistry.class);
        sut=new WebsocketConfig();
    }

    @Test
    public void testRegisterStompEndpoints(){
        sut.registerStompEndpoints(stompEndpointRegistryMock);
        verify(stompEndpointRegistryMock,times(1)).addEndpoint("/websocket");
    }

    @Test
    public void configureMessageBroker(){
        sut.configureMessageBroker(messageBrokerRegistryMock);
        verify(messageBrokerRegistryMock,times(1)).enableSimpleBroker("/topic");
        verify(messageBrokerRegistryMock,times(1)).setApplicationDestinationPrefixes("/app");

    }
}

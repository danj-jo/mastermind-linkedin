package com.example.mastermind.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class exists to intercept messages sent to my websocket connection. It takes the gameID (stored in a path variable) and stores it in a map along with the session. Websocket connections are unable to retrieve http query parameters, so having a message interceptor was the only way to do it besides sending the gameID itself in every api call.
 */
@Component
@AllArgsConstructor
@Getter
public class WebsocketChannel_Interceptor implements ChannelInterceptor {
    private final Map<String, String> sessionGameMap = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(WebsocketChannel_Interceptor.class);
    /**
     * This method intercepts STOMP frames sent through the WebSocket channel. It is invoked before a message is sent.
     * <p>
     * It extracts the STOMP session ID and game ID from the CONNECT frame's native headers and stores them in a map
     * for later cleanup during disconnect. This mapping allows multiplayer games to be tracked per session.
     * <p>
     * The channel parameter is required by the interface but is not used in this implementation.
     * The message parameter is used indirectly to extract headers via {@link StompHeaderAccessor}.
     *
     * @param message the STOMP message being intercepted
     * @param channel the message channel (unused)
     * @return the original message to continue processing
     */

    @Override
    public Message<?> preSend( @NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        String sessionId = accessor.getSessionId();
        StompCommand command = accessor.getCommand();

        if (sessionId == null || command == null){
            return message;
        }
// Here, I take the session and gameID before any guesses are sent to prevent null pointer exceptions in the event that games are disconnected before any guesses are sent. If the command is equal to CONNECT, then take the sessionId and gameID and store them in a map for deletion.
        if (StompCommand.CONNECT.equals(command)) {
            String gameId = accessor.getFirstNativeHeader("gameId");
            if (sessionId != null && gameId != null) {
                sessionGameMap.put(sessionId, gameId);
                logger.debug("Mapped session {} to game {} on CONNECT", sessionId, gameId);
            }
        }

        // If the command is disconnect, remove the sessionID from the map (and the game)
            if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
                sessionGameMap.remove(sessionId);
            }

        return message;
    }

    /**
     *
     * @param sessionId - the session ID to be removed.
     * @return - the value attached to the sessionID key. The value is currently unused, fpr I access the sessionID via accessor headers instead. It may be of use in the future.
     */
    public String removeSession(String sessionId) {
        return sessionGameMap.remove(sessionId);
    }


}

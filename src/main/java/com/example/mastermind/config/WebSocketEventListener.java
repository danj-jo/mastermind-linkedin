package com.example.mastermind.config;

import com.example.mastermind.services.MultiplayerGameService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import java.util.UUID;

/**
 * During development, I noticed that upon disconnection, games were still left inside the active games map (the map that I use to hold multiplayer games). To fix this issue, I created this class to listen for events fired by my websocket connection. Active games are stored in a different map <SessionID, GameID> and when there is a disconnection, I remove this session from the map, and I remove the game associated with the session from the active games map.
 */
@Component
@AllArgsConstructor
public class WebSocketEventListener {
    private final MultiplayerGameService multiplayerGameService;
    private final WebsocketChannel_Interceptor websocketChannelInterceptor;
    @EventListener
    public void handleDisconnectListener(SessionDisconnectEvent event) {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
            String sessionId = accessor.getSessionId();
            UUID gameId = UUID.fromString(websocketChannelInterceptor.getSessionGameMap().get(sessionId));
            multiplayerGameService.activeGames.remove(gameId);
            websocketChannelInterceptor.removeSession(sessionId);
            System.out.println(multiplayerGameService.activeGames);


    }

    /**
     *
     * @param event - the event that runs when the session is connected. This method isn't used currently, but it may be used if I decide to do anything upon connection.
     */
    @EventListener
    public void handleConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        System.out.println(multiplayerGameService.activeGames);
    }


}

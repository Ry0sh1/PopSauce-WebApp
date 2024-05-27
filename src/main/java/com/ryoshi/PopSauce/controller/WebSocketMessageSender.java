package com.ryoshi.PopSauce.controller;

import com.ryoshi.PopSauce.entity.Game;
import com.ryoshi.PopSauce.entity.Message;
import com.ryoshi.PopSauce.entity.MessageType;
import com.ryoshi.PopSauce.entity.Player;
import com.ryoshi.PopSauce.repository.*;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@CrossOrigin
public class WebSocketMessageSender {

    private final SimpMessageSendingOperations messagingTemplate;
    private final PlayerToGameRepository playerToGameRepository;
    private final PlayerRepository playerRepository;
    private final PictureToGameRepository pictureToGameRepository;
    private final SettingRepository settingRepository;
    private final GameRepository gameRepository;

    public WebSocketMessageSender(SimpMessageSendingOperations messagingTemplate,
                                  PlayerToGameRepository playerToGameRepository,
                                  PlayerRepository playerRepository,
                                  PictureToGameRepository pictureToGameRepository,
                                  SettingRepository settingRepository,
                                  GameRepository gameRepository) {
        this.messagingTemplate = messagingTemplate;
        this.playerToGameRepository = playerToGameRepository;
        this.playerRepository = playerRepository;
        this.pictureToGameRepository = pictureToGameRepository;
        this.settingRepository = settingRepository;
        this.gameRepository = gameRepository;
    }

    public void sendNewTimer(Game game, Message message){
        message.setGameCode(game.getCode());
        message.setSender(game.getCode());
        message.setMessageType(MessageType.TIME);
        message.setContent(String.valueOf(game.getCurrentTimer()));
        messagingTemplate.convertAndSend("/start-game/game/"+game.getCode(),message);
    }
    public void sendNewPicture(Game game, Message message) {
        message.setGameCode(game.getCode());
        message.setSender(game.getCode());
        message.setMessageType(MessageType.PICTURE);
        message.setContent(game.getCurrentPicture());
        messagingTemplate.convertAndSend("/start-game/game/"+game.getCode(),message);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        String gameCode = (String) headerAccessor.getSessionAttributes().get("gameCode");
        if (username != null) {
            System.out.println(username + " left the game");
            var chatMessage = Message.builder()
                    .gameCode(gameCode)
                    .messageType(MessageType.LEAVE)
                    .sender(username)
                    .build();
            Game game = gameRepository.findByCode(gameCode).orElseThrow();
            Player player = playerRepository.findByUsername(username);
            playerToGameRepository.deleteById(player.getId());
            playerRepository.delete(player);
            if (playerToGameRepository.findAllByGame(game).size() == 0){
                settingRepository.delete(game.getSetting());
                game.setHost(null);
                game.setSetting(null);
                pictureToGameRepository.deleteAll(pictureToGameRepository.findAllByGame(game));
                gameRepository.delete(game);
            }
            if (game.getHost() == player){
                game.setHost(playerToGameRepository.findAllByGame(game).get(0).getPlayer());
            }
            messagingTemplate.convertAndSend("/start-game/game/"+game.getCode(), chatMessage);
        }
    }

}

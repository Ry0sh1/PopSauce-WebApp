package com.ryoshi.PopSauce.controller;

import com.ryoshi.PopSauce.entity.*;
import com.ryoshi.PopSauce.repository.*;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;
import java.util.Objects;

@Component
public class WebSocketMessageSender {

    private final SimpMessageSendingOperations messagingTemplate;
    private final GamePlayerRepository gamePlayerRepository;
    private final PlayerRepository playerRepository;
    private final GamePictureRepository gamePictureRepository;
    private final SettingRepository settingRepository;
    private final GameRepository gameRepository;

    public WebSocketMessageSender(SimpMessageSendingOperations messagingTemplate,
                                  GamePlayerRepository gamePlayerRepository,
                                  PlayerRepository playerRepository,
                                  GamePictureRepository gamePictureRepository,
                                  SettingRepository settingRepository,
                                  GameRepository gameRepository) {
        this.messagingTemplate = messagingTemplate;
        this.gamePlayerRepository = gamePlayerRepository;
        this.playerRepository = playerRepository;
        this.gamePictureRepository = gamePictureRepository;
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
        String username = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("username");
        String gameCode = (String) headerAccessor.getSessionAttributes().get("gameCode");

        System.out.println("Someone Disconnected");
        System.out.println(username);
        if (username != null) {
            System.out.println(username + " left the game");
            var chatMessage = Message.builder()
                    .gameCode(gameCode)
                    .messageType(MessageType.LEAVE)
                    .sender(username)
                    .build();

            Game game = gameRepository.findByCode(gameCode).orElseThrow();
            Player player = playerRepository.findByUsername(username);

            List<GamePlayer> gamePlayers = gamePlayerRepository.findALlByPlayer(player);
            gamePlayerRepository.deleteAll(gamePlayers);

            if (!gamePlayerRepository.findAllByGame(game).isEmpty() && game.getHost().getUsername().equals(player.getUsername())){
                game.setHost(gamePlayerRepository.findAllByGame(game).get(0).getPlayer());
                gameRepository.save(game);
                messagingTemplate.convertAndSend("/start-game/game/"+game.getCode(), chatMessage);
            }

            if (gamePlayerRepository.findAllByGame(game).isEmpty()){
                gamePictureRepository.deleteAll(gamePictureRepository.findAllByGame(game));
                gameRepository.delete(game);
                settingRepository.deleteById(game.getSetting().getId());
            }

            playerRepository.deleteById(player.getId());
        }
    }

}

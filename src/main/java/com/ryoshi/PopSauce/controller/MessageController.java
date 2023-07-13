package com.ryoshi.PopSauce.controller;

import com.ryoshi.PopSauce.entity.Game;
import com.ryoshi.PopSauce.entity.Message;
import com.ryoshi.PopSauce.entity.MessageType;
import com.ryoshi.PopSauce.entity.Player;
import com.ryoshi.PopSauce.entity.PlayerToGame.PlayerToGame;
import com.ryoshi.PopSauce.repository.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Arrays;

@Controller
public class MessageController {

    private final PictureRepository pictureRepository;
    private final SettingRepository settingRepository;
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final PictureToGameRepository pictureToGameRepository;
    private final PlayerToGameRepository playerToGameRepository;

    public MessageController(PictureRepository pictureRepository,
                             SettingRepository settingRepository,
                             GameRepository gameRepository,
                             PlayerRepository playerRepository,
                             PictureToGameRepository pictureToGameRepository,
                             PlayerToGameRepository playerToGameRepository) {
        this.pictureRepository = pictureRepository;
        this.settingRepository = settingRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.pictureToGameRepository = pictureToGameRepository;
        this.playerToGameRepository = playerToGameRepository;
    }

    @MessageMapping("/game.wrongAnswer")
    @SendTo("/start-game/game")
    public Message wrongAnswer(@Payload Message message){
        return message;
    }

    @MessageMapping("/game.chat")
    @SendTo("/start-game/game")
    public Message chat(@Payload Message message, SimpMessageHeaderAccessor headerAccessor){
        if (message.getMessageType() == MessageType.JOIN){
            headerAccessor.getSessionAttributes().put("username",message.getSender());
            headerAccessor.getSessionAttributes().put("gameCode",message.getGameCode());
            Game game = gameRepository.findByCode(message.getGameCode()).orElseThrow();
            Player newPlayer = new Player();
            newPlayer.setUsername(message.getSender());
            newPlayer.setPoints(0);
            if (playerRepository.findByUsername(message.getSender()) != null){
                long id = playerRepository.findByUsername(message.getSender()).getId();
                newPlayer.setId(id);
                playerRepository.save(newPlayer);
            }else {
                playerRepository.save(newPlayer);
            }
            playerToGameRepository.save(new PlayerToGame(game,newPlayer));
        }
        return message;
    }

    @MessageMapping("/game.start")
    @SendTo("/start-game/game")
    public Message gameGotStarted(@Payload Message message){
        Game game = gameRepository.findByCode(message.getGameCode()).orElseThrow();
        if (message.getSender().equals(game.getHost().getUsername())){
            game.setStarted(true);
            gameRepository.save(game);
            return message;
        }else {
            return null;
        }
    }

}

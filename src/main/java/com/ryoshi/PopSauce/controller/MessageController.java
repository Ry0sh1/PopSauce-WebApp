package com.ryoshi.PopSauce.controller;

import com.ryoshi.PopSauce.entity.*;
import com.ryoshi.PopSauce.entity.GamePicture;
import com.ryoshi.PopSauce.entity.GamePlayer;
import com.ryoshi.PopSauce.repository.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.List;

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

    @MessageMapping("/game.wrongAnswer/{gameCode}")
    @SendTo("/start-game/game/{gameCode}")
    public Message wrongAnswer(@Payload Message message){
        return message;
    }

    @MessageMapping("/game.chat/{gameCode}")
    @SendTo("/start-game/game/{gameCode}")
    public Message chatMessage(@Payload Message message){
        return message;
    }

    @MessageMapping("/game.join/{gameCode}")
    @SendTo("/start-game/game/{gameCode}")
    public Message joinChat(@Payload Message message, SimpMessageHeaderAccessor headerAccessor){
        if (message.getMessageType() == MessageType.JOIN){
            headerAccessor.getSessionAttributes().put("username",message.getSender());
            headerAccessor.getSessionAttributes().put("gameCode",message.getGameCode());
            Game game = gameRepository.findByCode(message.getGameCode()).orElseThrow();
            Player newPlayer = new Player();
            newPlayer.setUsername(message.getSender());
            if (playerRepository.findByUsername(message.getSender()) != null){
                long id = playerRepository.findByUsername(message.getSender()).getId();
                newPlayer.setId(id);
                playerRepository.save(newPlayer);
            }else {
                playerRepository.save(newPlayer);
            }
            playerToGameRepository.save(new GamePlayer(game,newPlayer, 0));
        }
        return message;
    }

    @MessageMapping("/game.start/{gameCode}")
    @SendTo("/start-game/game/{gameCode}")
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

    @MessageMapping("/game.addPoints/{gameCode}")
    @SendTo("/start-game/game/{gameCode}")
    public Message addPoints(@Payload Message message){
        Player player = playerRepository.findByUsername(message.getSender());
        Game game = gameRepository.findByCode(message.getGameCode()).orElseThrow();
        GamePlayer gamePlayer = playerToGameRepository.findByPlayerAndGame(player,game);
        gamePlayer.setPoints(gamePlayer.getPoints()+((Integer) message.getContent()));
        if (gamePlayer.getPoints()>=100){
            message.setMessageType(MessageType.END);
            message.setContent(message.getSender() + " won the Game");
            game.setStarted(false);
            List<GamePlayer> allGamePlayer = playerToGameRepository.findAllByGame(game);
            for (GamePlayer playerGame: allGamePlayer) {
                playerGame.setPoints(0);
                playerToGameRepository.save(playerGame);
            }
            gameRepository.save(game);
        }else {
            playerToGameRepository.save(gamePlayer);
        }
        return message;
    }

    @MessageMapping("/game.playAgain/{gameCode}")
    @SendTo("/start-game/game/{gameCode}")
    public Message playAgain(@Payload Message message){

        Game game = gameRepository.findByCode(message.getGameCode()).orElseThrow();
        pictureToGameRepository.deleteAll(pictureToGameRepository.findAllByGame(game));

        List<Picture> pictures = pictureRepository.findAllByCategory(game.getSetting().getCategory());
        //Shuffle The list
        for (int i = 0;i<pictures.size();i++){
            Picture first = pictures.get(i);
            int random = (int) (Math.floor(Math.random()*pictures.size()));
            pictures.set(i,pictures.get(random));
            pictures.set(random,first);
        }
        //Insert List
        for (int i = 0;i < pictures.size();i++){
            pictureToGameRepository.save(new GamePicture(game,pictures.get(i),i));
        }

        game.setCurrentPicture(pictureToGameRepository.findByGameAndPlace(game,0).orElseThrow().getPicture());
        game.setCurrentTimer(game.getSetting().getGuessTimer()+game.getSetting().getResultTimer());

        game.setStarted(true);
        gameRepository.save(game);

        message.setMessageType(MessageType.PLAY_AGAIN);
        return message;
    }

}

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
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@Controller
@CrossOrigin
public class MessageController {

    private final PictureRepository pictureRepository;
    private final SettingRepository settingRepository;
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final GamePictureRepository gamePictureRepository;
    private final GamePlayerRepository gamePlayerRepository;

    public MessageController(PictureRepository pictureRepository,
                             SettingRepository settingRepository,
                             GameRepository gameRepository,
                             PlayerRepository playerRepository,
                             GamePictureRepository gamePictureRepository,
                             GamePlayerRepository gamePlayerRepository) {
        this.pictureRepository = pictureRepository;
        this.settingRepository = settingRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.gamePictureRepository = gamePictureRepository;
        this.gamePlayerRepository = gamePlayerRepository;
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
            gamePlayerRepository.save(new GamePlayer(game,newPlayer, 0));
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
        GamePlayer gamePlayer = gamePlayerRepository.findByPlayerAndGame(player,game);
        gamePlayer.setPoints(gamePlayer.getPoints()+((Integer) message.getContent()));
        if (gamePlayer.getPoints()>=100){
            message.setMessageType(MessageType.END);
            message.setContent(message.getSender() + " won the Game");
            game.setStarted(false);
            List<GamePlayer> allGamePlayer = gamePlayerRepository.findAllByGame(game);
            for (GamePlayer playerGame: allGamePlayer) {
                playerGame.setPoints(0);
                gamePlayerRepository.save(playerGame);
            }
            gameRepository.save(game);
        }else {
            gamePlayerRepository.save(gamePlayer);
        }
        return message;
    }

    @MessageMapping("/game.playAgain/{gameCode}")
    @SendTo("/start-game/game/{gameCode}")
    public Message playAgain(@Payload Message message){

        Game game = gameRepository.findByCode(message.getGameCode()).orElseThrow();
        gamePictureRepository.deleteAll(gamePictureRepository.findAllByGame(game));

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
            gamePictureRepository.save(new GamePicture(game,pictures.get(i),i));
        }

        game.setCurrentPicture(gamePictureRepository.findByGameAndPlace(game,0).orElseThrow().getPicture());
        game.setCurrentTimer(game.getSetting().getGuessTimer()+game.getSetting().getResultTimer());

        game.setStarted(true);
        gameRepository.save(game);

        message.setMessageType(MessageType.PLAY_AGAIN);
        return message;
    }

}

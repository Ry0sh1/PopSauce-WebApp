package com.ryoshi.PopSauce.controller;

import com.ryoshi.PopSauce.entity.*;
import com.ryoshi.PopSauce.entity.PictureToGame.PictureToGame;
import com.ryoshi.PopSauce.entity.PlayerToGame.PlayerToGame;
import com.ryoshi.PopSauce.repository.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
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

    @MessageMapping("/game.wrongAnswer")
    @SendTo("/start-game/game")
    public Message wrongAnswer(@Payload Message message){
        return message;
    }

    @MessageMapping("/game.chat")
    @SendTo("/start-game/game")
    public Message chatMessage(@Payload Message message){
        return message;
    }

    @MessageMapping("/game.join")
    @SendTo("/start-game/game")
    public Message joinChat(@Payload Message message, SimpMessageHeaderAccessor headerAccessor){
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

    @MessageMapping("/game.addPoints")
    @SendTo("/start-game/game")
    public Message addPoints(@Payload Message message){
        Player player = playerRepository.findByUsername(message.getSender());
        player.setPoints(player.getPoints()+((Integer) message.getContent()));
        if (player.getPoints()>=100){
            message.setMessageType(MessageType.END);
            message.setContent(message.getSender() + " won the Game");
            Game game = gameRepository.findByCode(message.getGameCode()).orElseThrow();
            game.setStarted(false);
            List<PlayerToGame> allPlayerToGame = playerToGameRepository.findAllByGame(game);
            for (PlayerToGame playerToGame:allPlayerToGame) {
                playerToGame.getPlayers().setPoints(0);
                playerRepository.save(playerToGame.getPlayers());
            }
            gameRepository.save(game);
        }else {
            playerRepository.save(player);
        }
        return message;
    }

    @MessageMapping("/game.playAgain")
    @SendTo("/start-game/game")
    public Message playAgain(@Payload Message message){

        Game game = gameRepository.findByCode(message.getGameCode()).orElseThrow();
        pictureToGameRepository.deleteAll(pictureToGameRepository.findAllByGames(game));

        List<Pictures> pictures = pictureRepository.findAllByCategory(game.getSetting().getCategory());
        //Shuffle The list
        for (int i = 0;i<pictures.size();i++){
            Pictures first = pictures.get(i);
            int random = (int) (Math.floor(Math.random()*pictures.size()));
            pictures.set(i,pictures.get(random));
            pictures.set(random,first);
        }
        //Insert List
        for (int i = 0;i < pictures.size();i++){
            pictureToGameRepository.save(new PictureToGame(game,pictures.get(i),i));
        }

        game.setCurrentPicture(pictureToGameRepository.findByGamesAndPlace(game,0).orElseThrow().getPictures());
        game.setCurrentTimer(game.getSetting().getGuessTimer()+game.getSetting().getResultTimer());

        game.setStarted(true);
        gameRepository.save(game);

        message.setMessageType(MessageType.START);
        return message;
    }

}

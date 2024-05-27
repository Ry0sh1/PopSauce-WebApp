package com.ryoshi.PopSauce.service;

import com.ryoshi.PopSauce.controller.MessageController;
import com.ryoshi.PopSauce.controller.WebSocketMessageSender;
import com.ryoshi.PopSauce.entity.Game;
import com.ryoshi.PopSauce.entity.Message;
import com.ryoshi.PopSauce.entity.GamePicture;
import com.ryoshi.PopSauce.repository.GameRepository;
import com.ryoshi.PopSauce.repository.PictureToGameRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TimerService {

    private final GameRepository gameRepository;
    private final PictureToGameRepository pictureToGameRepository;
    private final WebSocketMessageSender webSocketMessageSender;

    public TimerService(GameRepository gameRepository,
                        PictureToGameRepository pictureToGameRepository, MessageController messageController, WebSocketMessageSender webSocketMessageSender) {
        this.gameRepository = gameRepository;
        this.pictureToGameRepository = pictureToGameRepository;
        this.webSocketMessageSender = webSocketMessageSender;
    }

    @Scheduled(fixedRate = 1000) // Run every second
    public void decrementTimer() {
        List<Game> games = gameRepository.findAllByStarted(true);
        for (Game game:games) {
            if (game.getCurrentTimer()==0){
                GamePicture picture = pictureToGameRepository.findByGameAndPicture(game, game.getCurrentPicture()).orElseThrow();
                int newPlace = picture.getPlace() + 1;
                int amountOfPictures = pictureToGameRepository.findAllByGame(game).size();
                if (newPlace<amountOfPictures){
                    game.setCurrentPicture(pictureToGameRepository.findByGameAndPlace(game,newPlace).orElseThrow().getPicture());
                }else {
                    game.setCurrentPicture(pictureToGameRepository.findByGameAndPlace(game,0).orElseThrow().getPicture());
                }
                gameRepository.save(game);
                game.setCurrentTimer(game.getSetting().getGuessTimer()+game.getSetting().getResultTimer());
                webSocketMessageSender.sendNewPicture(game,new Message());
                webSocketMessageSender.sendNewTimer(game, new Message());
            }else {
                game.setCurrentTimer(game.getCurrentTimer()-1);
                gameRepository.save(game);
                webSocketMessageSender.sendNewTimer(game, new Message());
            }
            gameRepository.save(game);
        }
    }

}
package com.ryoshi.PopSauce.service;

import com.ryoshi.PopSauce.controller.PopSauce.MessageController;
import com.ryoshi.PopSauce.controller.PopSauce.WebSocketMessageSender;
import com.ryoshi.PopSauce.entity.Game;
import com.ryoshi.PopSauce.entity.Message;
import com.ryoshi.PopSauce.entity.GamePicture;
import com.ryoshi.PopSauce.repository.GameRepository;
import com.ryoshi.PopSauce.repository.GamePictureRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TimerService {

    private final GameRepository gameRepository;
    private final GamePictureRepository gamePictureRepository;
    private final WebSocketMessageSender webSocketMessageSender;

    public TimerService(GameRepository gameRepository,
                        GamePictureRepository gamePictureRepository, MessageController messageController, WebSocketMessageSender webSocketMessageSender) {
        this.gameRepository = gameRepository;
        this.gamePictureRepository = gamePictureRepository;
        this.webSocketMessageSender = webSocketMessageSender;
    }

    @Scheduled(fixedRate = 1000) // Run every second
    public void decrementTimer() {
        List<Game> games = gameRepository.findAllByStarted(true);
        for (Game game:games) {
            if (game.getCurrentTimer()==0){
                GamePicture picture = gamePictureRepository.findByGameAndPicture(game, game.getCurrentPicture()).orElseThrow();
                int newPlace = picture.getPlace() + 1;
                int amountOfPictures = gamePictureRepository.findAllByGame(game).size();
                if (newPlace<amountOfPictures){
                    game.setCurrentPicture(gamePictureRepository.findByGameAndPlace(game,newPlace).orElseThrow().getPicture());
                }else {
                    game.setCurrentPicture(gamePictureRepository.findByGameAndPlace(game,0).orElseThrow().getPicture());
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
package com.ryoshi.PopSauce.controller;

import com.google.gson.Gson;
import com.ryoshi.PopSauce.entity.Game;
import com.ryoshi.PopSauce.entity.Pictures;
import com.ryoshi.PopSauce.entity.Player;
import com.ryoshi.PopSauce.entity.Setting;
import com.ryoshi.PopSauce.factory.ImageFactory;
import com.ryoshi.PopSauce.repository.GameRepository;
import com.ryoshi.PopSauce.repository.PictureRepository;
import com.ryoshi.PopSauce.repository.PlayerRepository;
import com.ryoshi.PopSauce.repository.SettingRepository;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class TestController {

    private final PictureRepository pictureRepository;
    private final SettingRepository settingRepository;
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;

    public TestController(PictureRepository pictureRepository,
                          SettingRepository settingRepository,
                          GameRepository gameRepository,
                          PlayerRepository playerRepository) {
        this.pictureRepository = pictureRepository;
        this.settingRepository = settingRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    @GetMapping("/picture")
    public String getContent(){
        List<Pictures> pic = pictureRepository.findAllByCategory("Testing");
        Gson gson = new Gson();
        return gson.toJson(pic);
    }

    /*
    @PostMapping("/create")
    public void createGame(@RequestBody Game game){
        playerRepository.save(game.getHost());
        settingRepository.save(game.getSetting());
        gameRepository.save(game);
    }
     */

    @GetMapping("/insert-test-data-into-database")
    public void test(){
        List<File> files = ImageFactory.getFilesInFolder(new File("src/main/resources/pictures"));
        List<String> rightGuesses = new ArrayList<>();
        rightGuesses.add("Attack on Titan,AoT");
        rightGuesses.add("Attack on Titan,AoT");
        rightGuesses.add("Black Butler");
        rightGuesses.add("Bleach");
        rightGuesses.add("Bleach");
        rightGuesses.add("Chihiros Reise ins Zauberland");
        rightGuesses.add("Chihiros Reise ins Zauberland");
        rightGuesses.add("Clannad");
        rightGuesses.add("Clannad");
        rightGuesses.add("Code Geass");
        rightGuesses.add("Code Geass");
        rightGuesses.add("Cowboy Bepop");
        rightGuesses.add("Cowboy Bepop");
        rightGuesses.add("Demon Slayer");
        rightGuesses.add("Demon Slayer");
        rightGuesses.add("Fruits Basket");
        rightGuesses.add("Fruits Basket");
        rightGuesses.add("Fullmetal Alchemist Brotherhood");
        rightGuesses.add("Fullmetal Alchemist Brotherhood");
        rightGuesses.add("Gintama");
        rightGuesses.add("Gintama");
        rightGuesses.add("Haikyuu");
        rightGuesses.add("Hajime No Ippo");
        rightGuesses.add("Hajime No Ippo");
        rightGuesses.add("Hunter x Hunter");
        rightGuesses.add("Hunter x Hunter");
        rightGuesses.add("Kaguya Sama");
        rightGuesses.add("Kaguya Sama");
        rightGuesses.add("Made in Abyss");
        rightGuesses.add("Made in Abyss");
        rightGuesses.add("Mob Psycho");
        rightGuesses.add("Prinzessin Mononoke");
        rightGuesses.add("Prinzessin Mononoke");
        rightGuesses.add("One Piece");
        rightGuesses.add("One Piece");
        rightGuesses.add("Oshi no ko");
        rightGuesses.add("Oshi no ko");
        rightGuesses.add("Steins Gate");
        rightGuesses.add("Steins Gate");
        rightGuesses.add("Vinland Saga");
        rightGuesses.add("Vinland Saga");
        rightGuesses.add("Violet Evergarden");
        try {
            insertIntoDB(files,1L,rightGuesses);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertIntoDB(List<File> files, long starting, List<String> right_guess) throws IOException {

        int counter = 0;

        for (File file:files) {
            byte[] imageData = ImageFactory.getImageAsBytes(file);
            Pictures pic = new Pictures(starting,"Testing",imageData,right_guess.get(counter));
            pictureRepository.save(pic);
            starting++;
            counter++;
        }

    }

}
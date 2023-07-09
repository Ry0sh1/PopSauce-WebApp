package com.ryoshi.PopSauce.controller;

import com.google.gson.Gson;
import com.ryoshi.PopSauce.entity.Game;
import com.ryoshi.PopSauce.entity.PictureToGame.PictureToGame;
import com.ryoshi.PopSauce.entity.Pictures;
import com.ryoshi.PopSauce.entity.Player;
import com.ryoshi.PopSauce.factory.ImageFactory;
import com.ryoshi.PopSauce.repository.*;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class GameRestController {

    private final PictureRepository pictureRepository;
    private final SettingRepository settingRepository;
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final PictureToGameRepository pictureToGameRepository;

    public GameRestController(PictureRepository pictureRepository,
                              SettingRepository settingRepository,
                              GameRepository gameRepository,
                              PlayerRepository playerRepository,
                              PictureToGameRepository pictureToGameRepository) {
        this.pictureRepository = pictureRepository;
        this.settingRepository = settingRepository;
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.pictureToGameRepository = pictureToGameRepository;
    }

    @GetMapping("/next-picture/{code}/{currentPictureIndex}")
    public String getNextPicture(@PathVariable String code,@PathVariable int currentPictureIndex){
        Game game = gameRepository.findByCode(code).orElseThrow();
        Pictures currentPicture = pictureToGameRepository.findByGamesAndPlace(game,currentPictureIndex).orElseThrow().getPictures();//TODO: HANDEL OUT OF BOUNDATION EVENT
        game.setCurrentPicture(currentPicture);
        gameRepository.save(game);
        Gson gson = new Gson();
        return gson.toJson(currentPicture);
    }

    @GetMapping("/get-current-picture-index/{code}")
    public String getCurrentPictureIndex(@PathVariable String code){
        Game game = gameRepository.findByCode(code).orElseThrow();
        return String.valueOf(pictureToGameRepository.findByGamesAndPictures(game,game.getCurrentPicture())
                .orElseThrow().getPlace());
    }

    @GetMapping("/current-picture/{code}")
    public String getCurrentPicture(@PathVariable String code){
        Game game = gameRepository.findByCode(code).orElseThrow();
        if (game.getCurrentPicture() == null){
            game.setCurrentPicture(pictureToGameRepository.findByGamesAndPlace(game,0).orElseThrow().getPictures());
        }
        Pictures currentPicture = game.getCurrentPicture();
        Gson gson = new Gson();
        return gson.toJson(currentPicture);
    }

    @GetMapping("/current-timer/{code}")
    public String getCurrentTimer(@PathVariable String code){
        Game game = gameRepository.findByCode(code).orElseThrow();
        return String.valueOf(game.getCurrentTimer());
    }

    @PostMapping("/create")
    public String createGame(@RequestBody @NonNull Game game){
        Player host = game.getHost();

        //Make a Code
        String[] alphabet = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        StringBuilder code = new StringBuilder();
        long playerId;
        do{
            for (int i = 0;i<4;i++){
                code.append(alphabet[(int) (Math.random()*(alphabet.length))]);
            }
        }while (gameRepository.findByCode(code.toString()).isPresent());
        if (playerRepository.findByUsername(host.getUsername()) != null){
            playerId = playerRepository.findByUsername(host.getUsername()).getId();
            host.setId(playerId);
            playerRepository.save(host);
        }else {
            playerRepository.save(host);
        }

        //Save Settings
        settingRepository.save(game.getSetting());

        //Set Up Game
        game.setCode(code.toString());
        game.setCurrentTimer(0);
        game.setCurrentPicture(null);
        gameRepository.save(game);

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
        gameRepository.save(game);

        //Set Up the host
        host.setGame(game);
        playerRepository.save(host);
        return code.toString();
    }

    @GetMapping("/add-points/{username}")
    public void addPoints(@PathVariable String username){
        Player user = playerRepository.findByUsername(username);
        user.setPoints(user.getPoints() + 10);
        playerRepository.save(user);
    }

    @GetMapping("/join-game/{username}/{code}")
    public void joinGame(@PathVariable String username, @PathVariable String code){
        Game game = gameRepository.findByCode(code).orElseThrow();
        Player newPlayer = new Player();
        newPlayer.setGame(game);
        newPlayer.setUsername(username);
        newPlayer.setPoints(0);
        if (playerRepository.findByUsername(username) != null){
            long id = playerRepository.findByUsername(username).getId();
            newPlayer.setId(id);
            playerRepository.save(newPlayer);
        }else {
            playerRepository.save(newPlayer);
        }
        game.getPlayers().add(newPlayer);
    }

    @GetMapping("/get-points-of-user/{username}")
    public String getPoints(@PathVariable String username){
        return String.valueOf(playerRepository.findByUsername(username).getPoints());
    }

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
            insertIntoDB(files,rightGuesses);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/getAllPlayer/{code}")
    private String getAllPlayer(@PathVariable String code){
        Game game = gameRepository.findByCode(code).orElseThrow();
        List<Player> players = playerRepository.findAllByGame(game);
        StringBuilder json = new StringBuilder();
        json.append("[");
        for (Player player:players) {
            json.append("{\"username\":\"").append(player.getUsername()).append("\",\"points\":").append(player.getPoints()).append("},");
        }
        json.deleteCharAt(json.length()-1);
        json.append("]");
        //System.out.println(json); For testing purposes
        return json.toString();
    }

    @GetMapping("/clear-database")
    public void clearDatabase(){
        gameRepository.deleteAll();
        pictureToGameRepository.deleteAll();
        playerRepository.deleteAll();
        settingRepository.deleteAll();
    }

    private void insertIntoDB(List<File> files, List<String> right_guess) throws IOException {
        int counter = 0;
        for (File file:files) {
            byte[] imageData = ImageFactory.getImageAsBytes(file);
            Pictures pic = new Pictures("Testing",imageData,right_guess.get(counter));
            pictureRepository.save(pic);
            counter++;
        }

    }

}
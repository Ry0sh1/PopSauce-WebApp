package com.ryoshi.PopSauce.controller;

import com.google.gson.Gson;
import com.ryoshi.PopSauce.entity.Game;
import com.ryoshi.PopSauce.entity.PictureToGame.PictureToGame;
import com.ryoshi.PopSauce.entity.Pictures;
import com.ryoshi.PopSauce.entity.Player;
import com.ryoshi.PopSauce.entity.PlayerToGame.PlayerToGame;
import com.ryoshi.PopSauce.repository.PlayerToGameRepository;
import com.ryoshi.PopSauce.factory.ImageFactory;
import com.ryoshi.PopSauce.repository.*;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@RestController
public class GameRestController {

    private final PictureRepository pictureRepository;
    private final SettingRepository settingRepository;
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final PictureToGameRepository pictureToGameRepository;
    private final PlayerToGameRepository playerToGameRepository;

    public GameRestController(PictureRepository pictureRepository,
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

    @GetMapping("/is-code-valid/{code}")
    public boolean isCodeValid(@PathVariable String code){
        Game game = gameRepository.findByCode(code).orElse(null);
        return game != null;
    }

    @GetMapping("/is-username-valid/{username}")
    public boolean isUsernameValid(@PathVariable String username){
        Player player = playerRepository.findByUsername(username);
        return player == null;
    }

    @GetMapping("/get-current-picture/{code}")
    public String getCurrentPicture(@PathVariable String code){
        Game game = gameRepository.findByCode(code).orElseThrow();
        if (game.getCurrentPicture() == null){
            game.setCurrentPicture(pictureToGameRepository.findByGamesAndPlace(game,0).orElseThrow().getPictures());
        }
        Pictures currentPicture = game.getCurrentPicture();
        Gson gson = new Gson();
        return gson.toJson(currentPicture);
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
        playerToGameRepository.save(new PlayerToGame(game,host));
        playerRepository.save(host);
        return code.toString();
    }

    @GetMapping("/getAllPlayer/{code}")
    private String getAllPlayer(@PathVariable String code){
        Game game = gameRepository.findByCode(code).orElseThrow();
        List<PlayerToGame> players = playerToGameRepository.findAllByGame(game);
        StringBuilder json = new StringBuilder();
        json.append("[");
        for (PlayerToGame playerToGame:players) {
            json.append("{\"username\":\"").append(playerToGame.getPlayers().getUsername()).append("\",\"points\":").append(playerToGame.getPlayers().getPoints()).append("},");
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

    @GetMapping("/is-started/{code}")
    public String isStarted(@PathVariable String code){
        Game game = gameRepository.findByCode(code).orElseThrow();
        return String.valueOf(game.isStarted());
    }

    @GetMapping("/get-host/{code}")
    public String getHost(@PathVariable String code){
        return gameRepository.findByCode(code).orElseThrow().getHost().getUsername();
    }

    @GetMapping("/get-current-timer/{code}")
    public String getCurrentGameTimer(@PathVariable String code){
        Game game = gameRepository.findByCode(code).orElseThrow();
        return String.valueOf(game.getCurrentTimer());
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

    @GetMapping("/insert-flag-data-into-database")
    public void flagPictures(){
        String[] flagNames = {"af","eg","ax","al","dz","as","vi","ad","ao","ai","aq","ag","gq","ar","am",
                "aw","az","et","au","bs","bh","bd","bb","be","bz","bj","bm","bt","bo","ba","bw","bv","br",
                "vg","io","bn","bg","bf","bi","cl","cn","ck","cr","cw","dk","de","dm","do","dj","ec","sv",
                "ci","gb-eng","er","ee","fk","fo","fj","fi","fr","gf","pf","tf","ga","gm","ge","gh","gi",
                "gd","gr","gl","gp","gu","gt","gg","gn","gw","gy","ht","hm","hn","hk","in","id","im","iq",
                "ir","ie","is","il","it","jm","jp","ye","je","jo","ky","kh","cm","ca","cv","bq","kz","qa",
                "ke","kg","ki","um","cc","co","km","cg","cd","xk","hr","cu","kw","la","ls","lv","lb","lr",
                "ly","li","lt","lu","mo","mg","mw","my","mv","ml","mt","ma","mh","mq","mr","mu","yt","mx",
                "fm","md","mc","mn","me","ms","mz","mm", "na","nr","nc", "nz","ni","nl","ne","ng","nu",
                "gb-nir","kp","mp","mk","nf","no","om", "at","tl","pk","ps","pw","pa","pg","py","pe","ph",
                "pn","pl","pt","pr","re","rw","ro","ru","bl","mf","sb","zm","ws","sm","st","sa","gb-sct",
                "se","ch","sn","rs","sc","sl","zw","sg","sx","sk","si","so","es","sj","lk","sh","kn","lc",
                "pm","vc","za","sd","gs","kr","ss","sr","sz","sy","tj","tw","tz","th","tg","tk","to","tt",
                "td","cz","tn","tr","tm","tc","tv","ug","ua","hu","uy","uz","vu","va","ve","ae","us","gb",
                "vn","gb-wls","wf","cz","by","eh","cf","cy"};
        try {
            for (String flagName : flagNames) {
                ImageFactory.createImageFile(ImageFactory.getImage("https://flagcdn.com/w2560/" + flagName + ".png"), new URI("src/main/resources/pictures/Flags"), flagName);
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/debug")
    public void debugging(){



    }

    private void insertIntoDB(List<File> files, List<String> right_guess) throws IOException {
        int counter = 0;
        for (File file:files) {
            byte[] imageData = ImageFactory.getImageAsBytes(file);
            String base64Image = Base64.getEncoder().encodeToString(imageData);
            Pictures pic = new Pictures("Testing",base64Image,right_guess.get(counter));
            pictureRepository.save(pic);
            counter++;
        }

    }

}
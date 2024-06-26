package com.ryoshi.PopSauce.controller;

import com.ryoshi.PopSauce.entity.Picture;
import com.ryoshi.PopSauce.entity.Player;
import com.ryoshi.PopSauce.entity.Setting;
import com.ryoshi.PopSauce.factory.ImageFactory;
import com.ryoshi.PopSauce.repository.GameRepository;
import com.ryoshi.PopSauce.repository.PictureRepository;
import com.ryoshi.PopSauce.repository.PlayerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.HashSet;

@Controller
@CrossOrigin
public class BoardController {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final PictureRepository pictureRepository;

    public BoardController(GameRepository gameRepository,
                           PlayerRepository playerRepository,
                           PictureRepository pictureRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.pictureRepository = pictureRepository;
    }

    @PostMapping("/upload-own-picture")
    public String uploadOwnPicture(
            @RequestParam("url") String url,
            @RequestParam("category") String category,
            @RequestParam("rightGuesses") String rightGuesses,
            @RequestParam("difficulty") String difficulty){
        try {
            BufferedImage image = ImageFactory.getImage(url);
            insertIntoDB(image,category,rightGuesses,difficulty);
            category = category.replace(" ","");
            category = category.replace(".","");
            String[] rightGuessesArray = rightGuesses.split(",");
            File newFile = new File("src/main/resources/pictures/"+category);
            if (!newFile.exists()){
                newFile.mkdirs();
            }
            ImageFactory.createImageFile(image, new URI("src/main/resources/pictures/"+category), rightGuessesArray[0]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return "home";
    }

    private void insertIntoDB(BufferedImage image, String category, String right_guess, String difficulty) throws IOException {
        byte[] imageData = ImageFactory.getImageAsBytes(image);
        String base64Image = Base64.getEncoder().encodeToString(imageData);
        Picture pic = new Picture(category,base64Image,right_guess, difficulty);
        pictureRepository.save(pic);
    }

    @GetMapping("/create-game")
    public String getCreateGameWindow(Model model){
        model.addAttribute("settings", new Setting());
        HashSet<String> categories = new HashSet<>();
        Iterable<Picture> allPictures = pictureRepository.findAll();
        for (Picture p:allPictures) {
            categories.add(p.getCategory());
        }
        model.addAttribute("categories",categories);
        return "create-game";
    }

    @GetMapping("/start-game/{code}")
    public String startGame(@PathVariable String code, Model model){
        model.addAttribute("game",gameRepository.findByCode(code));
        return "in-game";
    }

    @GetMapping("")
    public String showHome(Model model){
        model.addAttribute("player",new Player());
        return "home";
    }

    @PostMapping("/set-username")
    public String setUsername(Player player){
        playerRepository.save(player);
        return "redirect:/";
    }

}

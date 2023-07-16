package com.ryoshi.PopSauce.controller;

import com.ryoshi.PopSauce.entity.Pictures;
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
import java.util.List;

@Controller
@RequestMapping("")
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
            @RequestParam("rightGuesses") String rightGuesses){
        try {
            BufferedImage image = ImageFactory.getImage(url);
            insertIntoDB(image,category,rightGuesses);
            category = category.replace(" ","");
            category = category.replace(".","");
            File newFile = new File("src/main/resources/pictures/"+category);
            if (!newFile.exists()){
                newFile.mkdirs();
            }
            ImageFactory.createImageFile(image, new URI("src/main/resources/pictures/"+category), rightGuesses);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return "home";
    }

    private void insertIntoDB(BufferedImage image, String category, String right_guess) throws IOException {
        byte[] imageData = ImageFactory.getImageAsBytes(image);
        String base64Image = Base64.getEncoder().encodeToString(imageData);
        Pictures pic = new Pictures(category,base64Image,right_guess);
        pictureRepository.save(pic);
    }

    @GetMapping("/create-game")
    public String getCreateGameWindow(Model model){
        model.addAttribute("settings", new Setting());
        HashSet<String> categories = new HashSet<>();
        Iterable<Pictures> allPictures = pictureRepository.findAll();
        for (Pictures p:allPictures) {
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

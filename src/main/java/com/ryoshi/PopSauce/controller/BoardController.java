package com.ryoshi.PopSauce.controller;

import com.ryoshi.PopSauce.entity.Pictures;
import com.ryoshi.PopSauce.entity.Player;
import com.ryoshi.PopSauce.entity.Setting;
import com.ryoshi.PopSauce.repository.GameRepository;
import com.ryoshi.PopSauce.repository.PictureRepository;
import com.ryoshi.PopSauce.repository.PlayerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;

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

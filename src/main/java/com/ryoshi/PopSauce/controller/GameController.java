package com.ryoshi.PopSauce.controller;

import com.ryoshi.PopSauce.entity.Game;
import com.ryoshi.PopSauce.entity.Player;
import com.ryoshi.PopSauce.entity.Setting;
import com.ryoshi.PopSauce.repository.GameRepository;
import com.ryoshi.PopSauce.repository.PlayerRepository;
import com.ryoshi.PopSauce.repository.SettingRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
public class GameController {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final SettingRepository settingRepository;

    public GameController(GameRepository gameRepository,
                          PlayerRepository playerRepository,
                          SettingRepository settingRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.settingRepository = settingRepository;
    }

    @GetMapping("/create-game/{username}")
    public String getCreateGameWindow(@PathVariable String username, Model model){
        playerRepository.save(new Player(username, 0));
        model.addAttribute("settings", new Setting());
        return "create-game";
    }

    @PostMapping("/creating")
    public String createGame(Setting setting){
        String[] alphabet = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        StringBuilder code = new StringBuilder();
        do{
            for (int i = 0;i<4;i++){
                code.append(alphabet[(int) (Math.random()*(alphabet.length))]);
            }
        }while (gameRepository.findByCode(code.toString()).isPresent());
        System.out.println(setting.getId());
        settingRepository.save(setting);
        gameRepository.save(new Game(
                code.toString(),
                playerRepository.findById(1L).orElseThrow(),
                0,
                null,
                setting
        ));
        return "redirect:/start-game";
    }

    @GetMapping("/start-game")
    public String startGame(){
        return "index";
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

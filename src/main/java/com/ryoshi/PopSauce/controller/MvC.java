package com.ryoshi.PopSauce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@CrossOrigin
public class MvC {

    @GetMapping("/")
    public String getDefault(){
        return "index";
    }

    @GetMapping("/index")
    public String getIndex(){
        return "index";
    }

    @GetMapping("/flappyBird")
    public String getFlappy(){
        return "flappyBird/flappyBird";
    }

    @GetMapping("/tron")
    public String getTron(){
        return "tron/tron";
    }

    @GetMapping("/frogger")
    public String getFrogger(){
        return "frogger/frogger";
    }

    @GetMapping("/space-invader")
    public String getSpaceInvader(){
        return "spaceInvader/spaceInvader";
    }

    @GetMapping("/snake")
    public String getSnake(){
        return "snake/snake";
    }

    @GetMapping("/centipede")
    public String getCentipede(){
        return "centipede/centipede";
    }
}

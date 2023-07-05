package com.ryoshi.PopSauce.controller;

import com.ryoshi.PopSauce.repository.PictureRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final PictureRepository pictureRepository;

    public TestController(PictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
    }

    @GetMapping("/text")
    public String getContent(){
        long d = (long) Math.floor(Math.random()*10);
        if (d == 0){
            d++;
        }
        return String.valueOf(pictureRepository.findAllByIdAndCategory(d,"Testing").getContent());
    }

}
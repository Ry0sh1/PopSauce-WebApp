package com.ryoshi.PopSauce.controller;

import com.google.gson.Gson;
import com.ryoshi.PopSauce.entity.Pictures;
import com.ryoshi.PopSauce.factory.ImageFactory;
import com.ryoshi.PopSauce.repository.PictureRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class TestController {

    private final PictureRepository pictureRepository;

    public TestController(PictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
    }

    @GetMapping("/picture")
    public String getContent(){
        List<Pictures> pic = pictureRepository.findAllByCategory("Testing");
        Gson gson = new Gson();
        return gson.toJson(pic);
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
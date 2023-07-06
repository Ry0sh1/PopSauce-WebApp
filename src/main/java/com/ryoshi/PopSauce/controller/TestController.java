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
        System.out.println(pic.size());
        List<byte[]> picturesAsBytes = new ArrayList<>();
        for (Pictures picture:pic) {
            picturesAsBytes.add(picture.getContent());
        }
        Gson gson = new Gson();
        String jsonString = gson.toJson(picturesAsBytes);
        System.out.println(jsonString);
        return jsonString;
    }

    private void insertIntoDB(List<File> files, long starting) throws IOException {

        for (File file:files) {
            byte[] imageData = ImageFactory.getImageAsBytes(file);
            Pictures pic = new Pictures(starting,"Testing",imageData);
            pictureRepository.save(pic);
            starting++;
        }

    }

}
package com.ryoshi.PopSauce.controller;

import com.ryoshi.PopSauce.entity.Pictures;
import com.ryoshi.PopSauce.factory.ImageFactory;
import com.ryoshi.PopSauce.repository.PictureRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@RestController
public class TestController {

    private final PictureRepository pictureRepository;

    public TestController(PictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
    }

    @GetMapping("/picture")
    public byte[] getContent(){
        long d = (long) Math.floor(Math.random()*8);
        if (d == 0){
            d++;
        }
        Pictures pic = pictureRepository.findById(d).orElseThrow();
        return pic.getContent();
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
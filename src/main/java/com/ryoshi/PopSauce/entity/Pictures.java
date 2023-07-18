package com.ryoshi.PopSauce.entity;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Value;

@Entity
public class Pictures {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String category;
    private String content;
    @Value("right_guess")
    private String rightGuess;
    private String difficulty;


    public Pictures(Long id, String category, String content, String rightGuess, String difficulty) {
        this.id = id;
        this.category = category;
        this.content = content;
        this.rightGuess = rightGuess;
        this.difficulty = difficulty;
    }

    public Pictures(String category, String content, String rightGuess) {
        this.category = category;
        this.content = content;
        this.rightGuess = rightGuess;
    }

    public Pictures(String category, String content, String rightGuess, String difficulty) {
        this.category = category;
        this.content = content;
        this.rightGuess = rightGuess;
        this.difficulty = difficulty;
    }

    public Pictures() {
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRightGuess() {
        return rightGuess;
    }

    public void setRightGuess(String rightGuess) {
        this.rightGuess = rightGuess;
    }
}

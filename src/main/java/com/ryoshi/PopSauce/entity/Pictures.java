package com.ryoshi.PopSauce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Entity
public class Pictures {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String category;
    private byte[] content;
    @Value("right_guess")
    private String rightGuess;

    public Pictures(Long id, String category, byte[] content, String rightGuess) {
        this.id = id;
        this.category = category;
        this.content = content;
        this.rightGuess = rightGuess;
    }

    public Pictures(String category, byte[] content, String rightGuess) {
        this.category = category;
        this.content = content;
        this.rightGuess = rightGuess;
    }

    public Pictures() {
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

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getRightGuess() {
        return rightGuess;
    }

    public void setRightGuess(String rightGuess) {
        this.rightGuess = rightGuess;
    }
}

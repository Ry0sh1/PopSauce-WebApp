package com.ryoshi.PopSauce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Picture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String category;
    @Column(columnDefinition = "TEXT")
    private String content;
    @Value("right_guess")
    private String rightGuess;
    private String difficulty;
    @OneToMany(mappedBy = "picture")
    private List<GamePicture> gamePictures;

    public Picture(Long id, String category, String content, String rightGuess, String difficulty) {
        this.id = id;
        this.category = category;
        this.content = content;
        this.rightGuess = rightGuess;
        this.difficulty = difficulty;
    }

    public Picture(String category, String content, String rightGuess) {
        this.category = category;
        this.content = content;
        this.rightGuess = rightGuess;
    }

    public Picture(String category, String content, String rightGuess, String difficulty) {
        this.category = category;
        this.content = content;
        this.rightGuess = rightGuess;
        this.difficulty = difficulty;
    }

}

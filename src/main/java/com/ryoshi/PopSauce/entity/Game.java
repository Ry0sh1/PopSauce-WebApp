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
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    @OneToOne
    private Player host;
    @Value("current_timer")
    private int currentTimer;
    @OneToOne
    @Value("current_picture")
    private Picture currentPicture;
    @OneToOne
    private Setting setting;
    private boolean started;
    @OneToMany(mappedBy = "game")
    private List<GamePicture> gamePictures;
    @OneToMany(mappedBy = "game")
    private List<GamePlayer> gamePlayers;

}

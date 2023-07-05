package com.ryoshi.PopSauce.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Game {

    @Id
    private Long id;
    private String code;
    @OneToOne
    private Player host;
    @OneToMany
    private List<Player> players;
    @OneToMany
    private List<Pictures> pictures;

}

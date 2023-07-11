package com.ryoshi.PopSauce.entity.PlayerToGame;

import com.ryoshi.PopSauce.entity.Game;
import com.ryoshi.PopSauce.entity.Player;
import jakarta.persistence.*;

@Entity
@IdClass(PlayerToGameId.class)
@Table(name = "game_players")
public class PlayerToGame {

    @Id
    @OneToOne
    private Game game;
    @Id
    @OneToOne
    private Player players;

    public PlayerToGame(Game game, Player players) {
        this.game = game;
        this.players = players;
    }

    public PlayerToGame() {
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayers() {
        return players;
    }

    public void setPlayers(Player players) {
        this.players = players;
    }

}

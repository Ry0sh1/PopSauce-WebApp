package com.ryoshi.PopSauce.entity.PlayerToGame;

import com.ryoshi.PopSauce.entity.Game;
import com.ryoshi.PopSauce.entity.Player;
import jakarta.persistence.*;

@Entity
@IdClass(PlayerToGameId.class)
@Table(name = "players_games")
public class PlayerToGame {

    @Id
    @OneToOne
    private Game games;
    @Id
    @OneToOne
    private Player players;

    public PlayerToGame(Game games, Player players) {
        this.games = games;
        this.players = players;
    }

    public PlayerToGame() {
    }

    public Game getGames() {
        return games;
    }

    public void setGames(Game games) {
        this.games = games;
    }

    public Player getPlayers() {
        return players;
    }

    public void setPlayers(Player players) {
        this.players = players;
    }

}

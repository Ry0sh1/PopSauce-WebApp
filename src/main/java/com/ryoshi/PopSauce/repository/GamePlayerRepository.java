package com.ryoshi.PopSauce.repository;

import com.ryoshi.PopSauce.entity.Game;
import com.ryoshi.PopSauce.entity.Player;
import com.ryoshi.PopSauce.entity.GamePlayer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GamePlayerRepository extends CrudRepository<GamePlayer, Long> {

    List<GamePlayer> findAllByGame(Game game);
    GamePlayer findByPlayer(Player player);
    GamePlayer findByPlayerAndGame(Player players, Game game);
    List<GamePlayer> findALlByPlayer(Player player);
}
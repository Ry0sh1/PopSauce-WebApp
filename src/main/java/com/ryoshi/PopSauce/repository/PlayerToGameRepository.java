package com.ryoshi.PopSauce.repository;

import com.ryoshi.PopSauce.entity.Game;
import com.ryoshi.PopSauce.entity.Player;
import com.ryoshi.PopSauce.entity.PlayerToGame.PlayerToGame;
import com.ryoshi.PopSauce.entity.PlayerToGame.PlayerToGameId;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PlayerToGameRepository extends CrudRepository<PlayerToGame, PlayerToGameId> {

    List<PlayerToGame> findAllByGame(Game game);
    PlayerToGame findByPlayers(Player player);

}
package com.ryoshi.PopSauce.repository;

import com.ryoshi.PopSauce.entity.Game;
import com.ryoshi.PopSauce.entity.PictureToGame.PictureToGame;
import com.ryoshi.PopSauce.entity.PictureToGame.PictureToGameId;
import com.ryoshi.PopSauce.entity.Pictures;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PictureToGameRepository extends CrudRepository<PictureToGame, PictureToGameId> {

    List<PictureToGame> findAllByGames(Game game);
    Optional<PictureToGame> findByGamesAndPlace(Game game, int place);
    Optional<PictureToGame> findByGamesAndPictures(Game game, Pictures picture);

}

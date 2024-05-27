package com.ryoshi.PopSauce.repository;

import com.ryoshi.PopSauce.entity.Game;
import com.ryoshi.PopSauce.entity.GamePicture;
import com.ryoshi.PopSauce.entity.Picture;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PictureToGameRepository extends CrudRepository<GamePicture, Long> {

    List<GamePicture> findAllByGame(Game game);
    Optional<GamePicture> findByGameAndPlace(Game game, int place);
    Optional<GamePicture> findByGameAndPicture(Game game, Picture picture);

    int findMaxPlaceByGame(Game games);

}

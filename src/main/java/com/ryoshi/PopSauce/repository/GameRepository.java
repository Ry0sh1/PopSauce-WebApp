package com.ryoshi.PopSauce.repository;

import com.ryoshi.PopSauce.entity.Game;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends CrudRepository<Game, Long> {

    Optional<Game> findByCode(String code);

    List<Game> findAllByStarted(Boolean started);

}

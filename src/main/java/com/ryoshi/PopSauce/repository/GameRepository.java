package com.ryoshi.PopSauce.repository;

import com.ryoshi.PopSauce.entity.Game;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends CrudRepository<Game, Long> {

    public Optional<Game> findByCode(String code);

}

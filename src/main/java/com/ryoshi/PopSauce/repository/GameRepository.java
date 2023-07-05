package com.ryoshi.PopSauce.repository;

import com.ryoshi.PopSauce.entity.Game;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends CrudRepository<Game, Long> {
    public List<Game> findAllByCode (String code); //test
}

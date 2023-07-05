package com.ryoshi.PopSauce.repository;

import com.ryoshi.PopSauce.entity.Game;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends CrudRepository<Game, Long> {
}

package com.ryoshi.PopSauce.repository;

import com.ryoshi.PopSauce.entity.Game;
import com.ryoshi.PopSauce.entity.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends CrudRepository<Player, Long> {

    Player findByUsername(String username);

}

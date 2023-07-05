package com.ryoshi.PopSauce.repository;

import com.ryoshi.PopSauce.entity.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends CrudRepository<Player, Long> {
}

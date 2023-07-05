package com.ryoshi.PopSauce.repository;

import com.ryoshi.PopSauce.entity.Pictures;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PictureRepository extends CrudRepository<Pictures, Long> {
}

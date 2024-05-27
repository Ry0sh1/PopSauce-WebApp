package com.ryoshi.PopSauce.repository;

import com.ryoshi.PopSauce.entity.Picture;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PictureRepository extends CrudRepository<Picture, Long> {

    List<Picture> findAllByCategory(String category);

}

package com.ryoshi.PopSauce.repository;

import com.ryoshi.PopSauce.entity.Pictures;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PictureRepository extends CrudRepository<Pictures, Long> {

    Pictures findAllByIdAndCategory(Long id, String category);

    List<Pictures> findAllByCategory(String category);

}

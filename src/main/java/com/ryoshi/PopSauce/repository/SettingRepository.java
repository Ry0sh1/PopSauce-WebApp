package com.ryoshi.PopSauce.repository;

import com.ryoshi.PopSauce.entity.Setting;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingRepository extends CrudRepository<Setting, Long> {
}

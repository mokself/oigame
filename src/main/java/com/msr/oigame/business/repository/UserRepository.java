package com.msr.oigame.business.repository;

import com.msr.oigame.business.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByKey(String key);
}

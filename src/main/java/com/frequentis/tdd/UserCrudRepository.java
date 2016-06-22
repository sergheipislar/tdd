package com.frequentis.tdd;

import org.springframework.data.repository.CrudRepository;

public interface UserCrudRepository extends CrudRepository<User, Long> {
    User findByEmail(String email);
}

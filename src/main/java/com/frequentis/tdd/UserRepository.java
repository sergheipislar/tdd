/*
 * COPYRIGHT: FREQUENTIS AG. All rights reserved.
 *            Registered with Commercial Court Vienna,
 *            reg.no. FN 72.115b.
 */
package com.frequentis.tdd;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    private final UserCrudRepository userCrudRepository;

    @Autowired
    public UserRepository(final UserCrudRepository userCrudRepository) {
        this.userCrudRepository = userCrudRepository;
    }

    public Optional<User> findByEmail(final String email) {
        return Optional.ofNullable(userCrudRepository.findByEmail(email));
    }

    public User save(final User user) {
        return userCrudRepository.save(user);
    }

    public List<User> findAll() {
        return Lists.newArrayList(userCrudRepository.findAll());
    }

    public User findOne(final Long id) {
        return userCrudRepository.findOne(id);
    }

    public boolean exists(final Long id) {
        return userCrudRepository.exists(id);
    }

    public void delete(final Long id) {
        userCrudRepository.delete(id);
    }
}

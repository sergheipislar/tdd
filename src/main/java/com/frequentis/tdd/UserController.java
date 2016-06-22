/*
 * COPYRIGHT: FREQUENTIS AG. All rights reserved.
 *            Registered with Commercial Court Vienna,
 *            reg.no. FN 72.115b.
 */
package com.frequentis.tdd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.frequentis.tdd.exceptions.EmailAlreadyUsedException;

@RestController
@RequestMapping("/user/")
public class UserController {
    private final UserRepository userRepository;

    @Autowired
    public UserController(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public User create(final @RequestBody User user) {
        if (! isEmailAlreadyUsedByOtherUser(user)) {
            return userRepository.save(user);
        } else {
            throw new EmailAlreadyUsedException();
        }
    }

    private boolean isEmailAlreadyUsedByOtherUser(final @RequestBody User user) {
        return userRepository.findByEmail(user.getEmail()) != null;
    }
}

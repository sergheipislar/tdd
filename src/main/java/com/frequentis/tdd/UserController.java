/*
 * COPYRIGHT: FREQUENTIS AG. All rights reserved.
 *            Registered with Commercial Court Vienna,
 *            reg.no. FN 72.115b.
 */
package com.frequentis.tdd;

import com.google.common.collect.Lists;
import java.util.List;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.frequentis.tdd.exceptions.EmailAlreadyUsedException;
import com.frequentis.tdd.exceptions.InvalidEmailException;
import com.frequentis.tdd.exceptions.UserNotFoundException;

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
        if (isValidEmailAddress(user.getEmail())) {
            if (!isEmailAlreadyUsedByOtherUser(user)) {
                return userRepository.save(user);
            } else {
                throw new EmailAlreadyUsedException();
            }
        } else {
            throw new InvalidEmailException();
        }
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @ResponseBody
    public List<User> getAll(){
        return Lists.newArrayList(userRepository.findAll());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public User get(@PathVariable Long id) {
        return userRepository.findOne(id);
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    @ResponseBody
    public User update(@RequestBody User user) {
        if (isValidEmailAddress(user.getEmail())) {
            if (userRepository.exists(user.getId())) {
                if (!isEmailAlreadyUsedByOtherUser(user)) {
                    return userRepository.save(user);
                } else {
                    throw new EmailAlreadyUsedException();
                }
            } else {
                throw new UserNotFoundException();
            }
        } else {
            throw new InvalidEmailException();
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void delete(@PathVariable Long id) {
        if (userRepository.exists(id)) {
            userRepository.delete(id);
        } else {
            throw new UserNotFoundException();
        }
    }

    private boolean isEmailAlreadyUsedByOtherUser(final @RequestBody User user) {
        User dbUser = userRepository.findByEmail(user.getEmail());
        return dbUser != null && ! dbUser.getId().equals(user.getId());
    }

    private boolean isValidEmailAddress(final String email) {
        return EmailValidator.getInstance().isValid(email);
    }
}

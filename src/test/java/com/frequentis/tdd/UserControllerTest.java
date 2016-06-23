/*
 * COPYRIGHT: FREQUENTIS AG. All rights reserved.
 *            Registered with Commercial Court Vienna,
 *            reg.no. FN 72.115b.
 */
package com.frequentis.tdd;

import com.google.common.collect.Lists;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import com.frequentis.tdd.data.Users;
import com.frequentis.tdd.exceptions.EmailAlreadyUsedException;
import com.frequentis.tdd.exceptions.UserNotFoundException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class UserControllerTest {
    private UserController sut;
    private UserRepository userRepository;

    @Before
    public void setUp(){
        userRepository = mock(UserRepository.class);
        sut = new UserController(userRepository);
    }

    @Test
    public void create_newUser_saveUserInRepository(){
        // Given
        User user = Users.random();

        // When
        sut.create(user);

        // Then
        verify(userRepository).save(user);
    }

    @Test
    public void create_newUser_returnSavedUser(){
        // Given
        User user = Users.random();
        User dbUser = Users.randomWithId();
        when(userRepository.save(user)).thenReturn(dbUser);

        // When
        User actualUser = sut.create(user);

        // Then
        assertThat("Expected user to match", actualUser, equalTo(dbUser));
    }

    @Test(expected = EmailAlreadyUsedException.class)
    public void create_userWithAlreadyUsedEmail_throwsEmailAlreadyUsed(){
        // Given
        User user = Users.random();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Users.randomWithId());

        // When
        sut.create(user);

        // Then
        // throws exception
    }

    @Test
    public void getAll_usersPresentInRepository_returnsUsersFromRepository(){
        // Given
        List<User> users = prepareUsersInRepository();

        // When
        List<User> actualUsers = sut.getAll();

        // Then
        assertThatUsersMatch(users, actualUsers);
    }

    @Test
    public void get_userPresent_returnFoundUser(){
        // Given
        User user = prepareUserInRepository();

        // When
        User actualUser = sut.get(user.getId());

        // Then
        assertThat("Expected user to match", actualUser, equalTo(user));
    }

    @Test
    public void update_userPresent_saveUserInRepository(){
        // Given
        User user = prepareUserInRepository();

        // When
        sut.update(user);

        // Then
        verify(userRepository).save(user);
    }

    @Test
    public void update_userPresent_returnUpdatedUser(){
        // Given
        User user = prepareUserInRepository();

        // When
        User actualUser = sut.update(user);

        // Then
        assertThat("Expected user to match", actualUser, equalTo(user));
    }

    @Test(expected = UserNotFoundException.class)
    public void update_userNotPresent_throwUserNotFoundException(){
        // Given
        User user = Users.randomWithId();

        // When
        sut.update(user);

        // Then
        // exception is thrown
    }

    @Test(expected = EmailAlreadyUsedException.class)
    public void update_withAlreadyExistingEmailAndUserPresent_throwsEmailAlreadyUsed(){
        // Given
        User user = prepareUserInRepository();
        User userForUpdate = prepareUserInRepository();
        userForUpdate.setEmail(user.getEmail());

        // When
        sut.update(userForUpdate);

        // Then
        // throws exception
    }

    @Test
    public void delete_userPresent_deleteUserFromRepository(){
        // Given
        User user = prepareUserInRepository();

        // When
        sut.delete(user.getId());

        // Then
        verify(userRepository).delete(user.getId());
    }

    @Test(expected = UserNotFoundException.class)
    public void delete_userNotPresent_throwsUserNotFoundException(){
        // Given
        User user = Users.randomWithId();

        // When
        sut.delete(user.getId());

        // Then
        // throws exception
    }

    private void assertThatUsersMatch(final List<User> users, final List<User> actualUsers) {
        assertThat("Expected all users returned from repository", actualUsers, hasSize(users.size()));
        for (User user : users) {
            assertThat("Expected user in returned result", actualUsers, hasItem(equalTo(user)));
        }
    }

    private List<User> prepareUsersInRepository() {
        List<User> users = Lists.newArrayList(Users.randomWithId(), Users.randomWithId());
        when(userRepository.findAll()).thenReturn(users);
        return users;
    }

    private User prepareUserInRepository() {
        User user = Users.randomWithId();
        when(userRepository.exists(user.getId())).thenReturn(true);
        when(userRepository.findOne(user.getId())).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        return user;
    }
}
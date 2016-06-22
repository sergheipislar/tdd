/*
 * COPYRIGHT: FREQUENTIS AG. All rights reserved.
 *            Registered with Commercial Court Vienna,
 *            reg.no. FN 72.115b.
 */
package com.frequentis.tdd;

import org.junit.Before;
import org.junit.Test;

import com.frequentis.tdd.data.Users;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
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
}
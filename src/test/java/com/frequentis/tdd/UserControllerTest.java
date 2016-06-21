package com.frequentis.tdd;

import com.frequentis.tdd.data.Users;
import org.junit.Before;
import org.junit.Test;

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
    public void create_newUser_storesUserInRepository(){
        // Given
        User user = Users.random();

        // When
        sut.create(user);

        // Then
        verify(userRepository).save(user);
    }

    @Test
    public void create_newUser_returnsNewlyCreatedUser(){
        // Given
        User newUser = Users.random();
        User dbUser = prepareNewUserInRepository(newUser);

        // When
        User actualUser = sut.create(newUser);

        // Then
        assertThat("Expected user to match", actualUser, equalTo(dbUser));
    }

    private User prepareNewUserInRepository(final User user) {
        User dbUser = Users.randomWithId();
        when(userRepository.save(user)).thenReturn(dbUser);
        return dbUser;
    }
}
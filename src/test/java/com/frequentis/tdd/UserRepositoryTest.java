/*
 * COPYRIGHT: FREQUENTIS AG. All rights reserved.
 *            Registered with Commercial Court Vienna,
 *            reg.no. FN 72.115b.
 */
package com.frequentis.tdd;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.frequentis.tdd.data.Randoms;
import com.frequentis.tdd.data.Users;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class UserRepositoryTest {
    private UserRepository sut;
    private UserCrudRepository userCrudRepository;

    @Before
    public void setUp(){
        userCrudRepository = mock(UserCrudRepository.class);
        sut = new UserRepository(userCrudRepository);
    }

    @Test
    public void findByEmail_userPresentInRepository_returnUserFromCrudRepository() throws Exception {
        // Given
        User user = prepareUserInRepository();

        // When
        Optional<User> actualUser = sut.findByEmail(user.getEmail());

        // Then
        assertThat("Expected user to match", actualUser.get(), equalTo(user));
    }

    @Test
    public void findByEmail_userNotPresentInRepository_returnOptionalEmpty() throws Exception {
        // Given
        User user = Users.randomWithId();

        // When
        Optional<User> actualUser = sut.findByEmail(user.getEmail());

        // Then
        assertThat("Expected user not present", actualUser.isPresent(), equalTo(false));
    }

    @Test
    public void save_user_returnSavedUsed() throws Exception {
        // Given
        User user = prepareUserInRepository();

        // When
        User actualUser = userCrudRepository.save(user);

        // Given
        assertThat("Expected user to match", actualUser, equalTo(user));
    }

    @Test
    public void findAll_usersInRepository_returnUsersFromRepository() throws Exception {
        // Given
        Iterable<User> users = Lists.newArrayList(Users.randomWithId(), Users.randomWithId());
        when(userCrudRepository.findAll()).thenReturn(users);

        // When
        List<User> actualUsers = sut.findAll();

        // Given
        assertThat("Expected user list to match", actualUsers, equalTo(users));
    }

    @Test
    public void findOne_userPresent_returnsUserFromRepository() throws Exception {
        // Given
        User user = prepareUserInRepository();

        // When
        User actualUser = sut.findOne(user.getId());

        // Then
        assertThat("Expected user to match", actualUser, equalTo(user));
    }

    @Test
    @Parameters({"TRUE", "FALSE"})
    public void exists_userPresent_returnTrue(final boolean exists) throws Exception {
        // Given
        User user = Users.randomWithId();
        when(userCrudRepository.exists(user.getId())).thenReturn(exists);

        // When
        boolean actualResult = sut.exists(user.getId());

        // Then
        assertThat("Expected user present in repository", actualResult, equalTo(exists));
    }

    @Test
    public void delete_userId_deletesUserFromRepository() throws Exception {
        // Given
        Long id = Randoms.randomLong();

        // When
        sut.delete(id);

        // Then
        verify(userCrudRepository).delete(id);
    }

    private User prepareUserInRepository() {
        User user = Users.randomWithId();
        when(userCrudRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(userCrudRepository.save(user)).thenReturn(user);
        when(userCrudRepository.findOne(user.getId())).thenReturn(user);
        return user;
    }
}
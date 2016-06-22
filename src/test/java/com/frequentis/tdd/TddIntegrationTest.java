/*
 * COPYRIGHT: FREQUENTIS AG. All rights reserved.
 *            Registered with Commercial Court Vienna,
 *            reg.no. FN 72.115b.
 */
package com.frequentis.tdd;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import com.frequentis.tdd.data.Randoms;
import com.frequentis.tdd.data.Users;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class, initializers = ConfigFileApplicationContextInitializer.class)
@WebAppConfiguration
public class TddIntegrationTest {
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
                                                  MediaType.APPLICATION_JSON.getSubtype(),
                                                  Charset.forName("utf8"));

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                                                         .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                                                         .findAny()
                                                         .get();

        Assert.assertNotNull("the JSON message converter must not be null", this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setup(){
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void insert_jsonUser_respondsWithNewlyCreatedUser() throws Exception {
        // Given
        User user = Users.random();
        String userJson = json(user);

        // When
        MvcResult mvcResult = mockMvc.perform(post("/user/").contentType(contentType).content(userJson)).andExpect(status().isOk()).andReturn();

        // Then
        User actualUser = fromJson(mvcResult.getResponse().getContentAsString());
        assertThat("Expected id to be set", actualUser.getId(), not(equalTo(0L)));
    }

    @Test
    public void getAll_usersPresent_respondsWithAListOfUsers() throws Exception {
        // Given
        User user1 = prepareUserInRepository();
        User user2 = prepareUserInRepository();

        // When
        MvcResult mvcResult = mockMvc.perform(get("/user/all").contentType(contentType)).andExpect(status().isOk()).andReturn();

        // Then
        assertThatResponseContainsUsers(mvcResult, user1, user2);
    }

    @Test
    public void getOne_userPresent_respondsWithFoundUser() throws Exception {
        // Given
        User user = prepareUserInRepository();

        // When
        MvcResult mvcResult = mockMvc.perform(get("/user/"+user.getId()).contentType(contentType)).andExpect(status().isOk()).andReturn();

        // Then
        User actualUser = fromJson(mvcResult.getResponse().getContentAsString());
        assertThat("Expected user to match", actualUser, equalTo(user));
    }

    @Test
    public void update_userPresent_respondsWithUpdatedUser() throws Exception {
        // Given
        User user = prepareUserInRepository();
        user.setLastName(Randoms.randomAlphanumeric("lastName_"));

        // When
        MvcResult mvcResult = mockMvc.perform(put("/user/").contentType(contentType).content(json(user))).andExpect(status().isOk()).andReturn();

        // Then
        User actualUser = fromJson(mvcResult.getResponse().getContentAsString());
        assertThat("Expected user to match", actualUser, equalTo(user));
    }

    @Test
    public void delete_userPresent_respondsWithOk() throws Exception {
        // Given
        User user = prepareUserInRepository();

        // When/Then
        mockMvc.perform(delete("/user/"+user.getId()).contentType(contentType)).andExpect(status().isOk());
    }

    private void assertThatResponseContainsUsers(final MvcResult mvcResult, final User... users) throws IOException {
        List<User> actualUsers = listFromJson(mvcResult.getResponse().getContentAsString());
        for (User user : users) {
            assertThat("Expected actual result contains the user", actualUsers, hasItem(hasProperty("id", equalTo(user.getId()))));
        }
    }

    private User prepareUserInRepository() throws Exception {
        String userJson = json(Users.random());
        MvcResult mvcResult = mockMvc.perform(post("/user/").contentType(contentType).content(userJson)).andExpect(status().isOk()).andReturn();
        return fromJson(mvcResult.getResponse().getContentAsString());
    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    protected User fromJson(String json) throws IOException {
        MockHttpInputMessage mockHttpInputMessage = new MockHttpInputMessage(json.getBytes());
        return (User) this.mappingJackson2HttpMessageConverter.read(User.class, mockHttpInputMessage);
    }

    protected List<User> listFromJson(String json) throws IOException {
        MockHttpInputMessage mockHttpInputMessage = new MockHttpInputMessage(json.getBytes());
        return Arrays.asList((User[]) this.mappingJackson2HttpMessageConverter.read(User[].class, mockHttpInputMessage));
    }
}

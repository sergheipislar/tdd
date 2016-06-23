/*
 * COPYRIGHT: FREQUENTIS AG. All rights reserved.
 *            Registered with Commercial Court Vienna,
 *            reg.no. FN 72.115b.
 */
package com.frequentis.tdd;

import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import com.frequentis.tdd.data.Randoms;
import com.frequentis.tdd.data.Users;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class, initializers = ConfigFileApplicationContextInitializer.class)
@WebAppConfiguration
@TestPropertySource(properties = "com.frequentis.tdd.filePath=D:\\\\tdd_test")
public class TddIntegrationTest {
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
                                                  MediaType.APPLICATION_JSON.getSubtype(),
                                                  Charset.forName("utf8"));

    @Value("${com.frequentis.tdd.filePath}")
    private String filePath;

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
    public void insert_jsonUser_respondsWithOk() throws Exception {
        // Given
        User user = Users.random();
        String userJson = json(user);

        // When/Then
        mockMvc.perform(post("/user/").contentType(contentType).content(userJson)).andExpect(status().isOk());
    }

    @Test
    public void getAll_usersPresent_respondsWithOk() throws Exception {
        // Given
        prepareCreatedUser();
        prepareCreatedUser();

        // When / Then
        mockMvc.perform(get("/user/all").contentType(contentType)).andExpect(status().isOk());
    }

    @Test
    public void getOne_userPresent_respondsWithOk() throws Exception {
        // Given
        User user = prepareCreatedUser();

        // When/Then
        mockMvc.perform(get("/user/" + user.getId()).contentType(contentType)).andExpect(status().isOk());
    }

    @Test
    public void update_userPresent_respondsWithOk() throws Exception {
        // Given
        User user = prepareCreatedUser();
        user.setLastName(Randoms.randomAlphanumeric("lastName_"));

        // When/Then
        mockMvc.perform(put("/user/").contentType(contentType).content(json(user))).andExpect(status().isOk());
    }

    @Test
    public void delete_userPresent_respondsWithOk() throws Exception {
        // Given
        User user = prepareCreatedUser();

        // When/Then
        mockMvc.perform(delete("/user/" + user.getId()).contentType(contentType)).andExpect(status().isOk());
    }

    @Test
    public void upload_image_respondsWithOk() throws Exception {
        // Given
        prepareFileStorage();

        InputStream resourceAsStream = getClass().getResourceAsStream("/profile.png");
        byte[] imageBytes = ByteStreams.toByteArray(resourceAsStream);

        // When / Then
        mockMvc.perform(fileUpload("/user/uploadImage").file("file", imageBytes)).andExpect(status().isOk());
        assertThat("Expected file to be stored", Files.newDirectoryStream(Paths.get(filePath)).iterator().hasNext(), equalTo(true));

        clearFileStorage();
    }

    private void clearFileStorage() throws IOException {
        Files.newDirectoryStream(Paths.get(filePath)).forEach(
                file -> {
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        Files.deleteIfExists(Paths.get(filePath));
    }

    private void prepareFileStorage() throws IOException {
        if (!Files.exists(Paths.get(filePath))) {
            Files.createDirectory(Paths.get(filePath));
        }
    }

    private User prepareCreatedUser() throws Exception {
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
}

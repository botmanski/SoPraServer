package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.LoginPostDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
        // given
        User user = new User();
        user.setName("Firstname Lastname");
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);

        List<User> allUsers = Collections.singletonList(user);

        // this mocks the UserService -> we define above what the userService should return when getUsers() is called
        given(userService.getUsers()).willReturn(allUsers);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(user.getName())))
                .andExpect(jsonPath("$[0].username", is(user.getUsername())))
                .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));
    }

    @Test
    public void createUser_validInput_userCreated() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setName("Test User");
        userPostDTO.setUsername("testUsername");

        given(userService.createUser(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
    }

    @Test
    public void tokenDoesGetSet() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setName("gaggi");
        user.setUsername("botman");
        user.setPassword("darkknight");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);

        UserGetDTO userGetDTO = new UserGetDTO();
        userGetDTO.setToken(user.getToken());

        given(userService.getUserByToken(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userGetDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.token", is(user.getToken())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
    }

    @Test
    public void logout() throws Exception {
        // given
        User user = new User();
        user.setId(1L);

        UserGetDTO userGetDTO = new UserGetDTO();
        userGetDTO.setToken(user.getToken());

        given(userService.getUserByToken(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = put("/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userGetDTO));

        // then
        mockMvc.perform(postRequest).andExpect(status().isNoContent());
    }

    @Test
    public void loginTest() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setPassword("test");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);

        LoginPostDTO loginPostDTO = new LoginPostDTO();
        loginPostDTO.setPassword(user.getPassword());
        loginPostDTO.setUsername(user.getUsername());

        given(userService.Login(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(loginPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.token", is(user.getToken())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
    }

    @Test
    public void get_nonExistingUserById() throws Exception {
        // given
        MockHttpServletRequestBuilder getRequest = get("/users/1").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isNotFound()).andExpect(status().is4xxClientError());
    }

    /*
    @Test
    public void changeNonExistingUser() throws Exception {
        Date currentDate = new Date();
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setName("Test User");
        userPostDTO.setUsername("testUsername");


        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(putRequest).andExpect(status().isNotFound()).andExpect(status().is4xxClientError());
    }

    @Test
    public void changeExistingUser() throws Exception {
        String currentDate = new Date().toString();
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setName("Test User");
        userPutDTO.setUsername("testUsername");
        userPutDTO.setBirthDay(currentDate);

        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setPassword("test");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);
        given(userService.edit(Mockito.anyLong(), Mockito.any())).willReturn(user);
        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/edit/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        // then
        mockMvc.perform(putRequest).andExpect(status().isNoContent());
    }
     */

    @Test
    public void getUserById() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setPassword("test");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);
        given(userService.findUserById(Mockito.anyLong())).willReturn(user);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setName("Test User");
        userPostDTO.setUsername("testUsername");
        userPostDTO.setPassword("test");


        MockHttpServletRequestBuilder getRequest = get("/users/1").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.token", is(user.getToken())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
    }

    /**
     * Helper Method to convert userPostDTO into a JSON string such that the input can be processed
     * Input will look like this: {"name": "Test User", "username": "testUsername"}
     * @param object
     * @return string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The request body could not be created.%s", e.toString()));
        }
    }


}
package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UsersGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.LoginPostDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import org.springframework.data.annotation.QueryAnnotation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to the user.
 * The controller will receive the request and delegate the execution to the UserService and finally return the result.
 */
@RestController
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 201 - CREATED    check
     * 409 - CONFLICT
     * 200 - OK         check
     * 404 - NOT_FOUND
     * 204 - NO_CONTENT check
     **/

    // 1
    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UsersGetDTO> getAllUsers() {
        // fetch all users in the internal representation
        List<User> users = userService.getUsers();
        List<UsersGetDTO> usersGetDTO = new ArrayList<UsersGetDTO>();

        // convert each user to the API representation
        for (User user : users) {
            usersGetDTO.add(DTOMapper.INSTANCE.convertEntityToUsersGetDTO(user));
        }
        return usersGetDTO;
    }

    // 2
    // the Endpoint is created here
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        // create user
        User createdUser = userService.createUser(userInput);
        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
    }

    // 3
    // creating login Endpoint
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserGetDTO createUser(@RequestBody LoginPostDTO loginPostDTO) {
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertLoginPostDTOtoEntity(loginPostDTO);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(userService.Login(userInput));
    }

    // 4
    // getting user by id if there can be found a match, else throws error-message
    //TODO: not finished yet, what do I have to do in this one?
    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO getUserById(@PathVariable("userId") long userId) {
        User user = userService.findUserById(userId);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
    }

    // 5
    // logout Controller
    //TODO: not finished yet
    // create logout Endpoint
    // Simply put, the @RequestBody annotation maps the HttpRequest body to a transfer or domain object,
    // enabling automatic deserialization of the inbound HttpRequest body onto a Java object.
    @PutMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public UserGetDTO logoutUser(@RequestBody LoginPostDTO loginPostDTO) {
        User userInput = DTOMapper.INSTANCE.convertLoginPostDTOtoEntity(loginPostDTO);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(userService.Logout(userInput));
    }
}

    /*
    // 6
    // edit Controller
    //TODO: not finished yet
    @PutMapping("/users/{usersId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public UserGetDTO editUser(@RequestBody UserGetDTO userGetDTO) {
        User userInput = DTOMapper.INSTANCE.conver
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(userService.edit(userInput));
    }
}
     */

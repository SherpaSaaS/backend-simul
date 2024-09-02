package org.sherpaengineering.controllers;


import org.sherpaengineering.dtos.CreateUserRequestBody;
import org.sherpaengineering.models.Role;
import org.sherpaengineering.models.User;
import org.sherpaengineering.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@CrossOrigin("*")
@RequestMapping("/api/user")
@EnableDiscoveryClient
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/findById/{id}")
    public ResponseEntity<User> findById(@PathVariable("id") Integer userId ){
        try{
            return new ResponseEntity<>(
                    userService.findUserById(userId),
                    HttpStatus.OK
            );
        }catch (RuntimeException exception){
            return new ResponseEntity<>(null ,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/findAll")
    public ResponseEntity<List<User>> findAll(){
        return new ResponseEntity<>(userService.findAll() , HttpStatus.OK);
    }

    @PostMapping("/updateUser")
    public ResponseEntity<User> updateUser(@RequestBody User user){
        try{
            return new ResponseEntity<>(userService.updateUser(user) , HttpStatus.OK);
        }catch (RuntimeException e){
            return new ResponseEntity<>(null , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/createUser")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequestBody createUserRequestBody){
        User user = new User(
                null,
                createUserRequestBody.getFirstName(),
                createUserRequestBody.getLastName(),
                createUserRequestBody.getEmail(),
                createUserRequestBody.getPassword(),
                Role.valueOf(createUserRequestBody.getRole()),
                true
        );
        try{
            return new ResponseEntity<>(userService.addUser(user) , HttpStatus.OK);
        }catch (RuntimeException e){
            return new ResponseEntity<>(null , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/verifyUser/{id}")
    public ResponseEntity<User> verifyUser(@PathVariable("id") Integer userId ){
        try{
            return new ResponseEntity<>(userService.verifyUser(userId) , HttpStatus.OK);
        }catch (RuntimeException e){
            return new ResponseEntity<>(null , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

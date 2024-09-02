package org.sherpaengineering.services;

import org.sherpaengineering.models.User;
import org.sherpaengineering.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;


    public User addUser(User user){
        Optional<User> userOptional =  userRepository.findByEmail(user.getEmail());
        if(userOptional.isPresent()){
            throw new RuntimeException("user exist already");
        }
        return userRepository.save(user);
    }

    public User findUserById(Integer id){
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isPresent()){
            return userOptional.get();
        }else{
            throw new RuntimeException("User not found");
        }
    }

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public User updateUser(User user){
        Optional<User> userOptional = userRepository.findById(user.getId());
        if(userOptional.isPresent()){
            userRepository.save(user);
        }else{
            throw new RuntimeException("user dosen't exist to update");
        }
        return null;
    }

    public User verifyUser(Integer id){
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            user.setVerified(true);
            userRepository.save(user);
        }else{
            throw new RuntimeException("user dosen't exist to update");
        }
        return null;
    }
}

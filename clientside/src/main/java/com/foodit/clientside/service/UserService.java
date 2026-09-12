package com.foodit.clientside.service;
import com.foodit.clientside.entity.User;
import com.foodit.clientside.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class UserService {

    private UserRepository userRepository;

    public UserService( UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User createUser(User user){
        return userRepository.save(user);
    }

    public List<User> findAllUser(){
        return userRepository.findAll();
    }

}

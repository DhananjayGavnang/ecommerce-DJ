package com.djecommerce.service;

import com.djecommerce.config.JwtProvider;
import com.djecommerce.exception.UserException;
import com.djecommerce.model.User;
import com.djecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class UserServiceImplementation implements UserService{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public User findUserById(Long userId) throws UserException {
        Optional<User> existingUser = userRepository.findById(userId);
        if(existingUser.isPresent()){
            return existingUser.get();
        }
        throw new UserException("User not found with id " + userId);
    }

    @Override
    public User findUserProfileByJwt(String jwt) throws UserException {
        String email = jwtProvider.getEmailFromToken(jwt);
        User user = userRepository.findByEmail(email);
        if(user==null){
            throw new UserException("User not found with email " + email);
        }
        return user;
    }
}

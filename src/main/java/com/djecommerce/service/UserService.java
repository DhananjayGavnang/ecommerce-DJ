package com.djecommerce.service;

import com.djecommerce.exception.UserException;
import com.djecommerce.model.User;

public interface UserService {
	
	public User findUserById(Long userId) throws UserException;

	public User findUserProfileByJwt(String jwt) throws  UserException;

}

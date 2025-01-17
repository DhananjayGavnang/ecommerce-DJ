package com.djecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.djecommerce.config.JwtProvider;
import com.djecommerce.exception.UserException;
import com.djecommerce.model.User;
import com.djecommerce.repository.UserRepository;
import com.djecommerce.requestDTO.LoginRequest;
import com.djecommerce.responseDTO.AuthResponse;
import com.djecommerce.service.CustomUserDetailsService;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JwtProvider jwtProvider;	
	@Autowired
	private PasswordEncoder passwordEncoder;	
	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@PostMapping("/singup")
	public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws UserException {

		String email = user.getEmail();
		String password = user.getPassword();
		String firstName = user.getFirstName();
		String lastName = user.getLastName();

		User isEmailExist = userRepository.findByEmail(email);
		if (isEmailExist != null) {
			throw new UserException("Email is already used with another account");
		}

		User createdUser = new User();
		createdUser.setEmail(email);
		createdUser.setPassword(passwordEncoder.encode(password));
		createdUser.setFirstName(firstName);
		createdUser.setLastName(lastName);

		User savedUser = userRepository.save(createdUser);

		Authentication authentication = new UsernamePasswordAuthenticationToken(savedUser.getEmail(),
				savedUser.getPassword());
		SecurityContextHolder.getContext().setAuthentication(authentication);

		String token = jwtProvider.generateToken(authentication);

		AuthResponse authResponse = new AuthResponse();
		authResponse.setJwt(token);
		authResponse.setMessage("SignUp Success!!");
		return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.CREATED);

	}

	@PostMapping("/signin")
	public ResponseEntity<AuthResponse> loginUserhandler(@RequestBody LoginRequest loginRequest) {

		String username = loginRequest.getEmail();
		String password = loginRequest.getPassword();

		Authentication authentication = authenticate(username, password);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		String token = jwtProvider.generateToken(authentication);

		AuthResponse authResponse = new AuthResponse();
		authResponse.setJwt(token);
		authResponse.setMessage("SignUp Success!!");
		return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.CREATED);

	}

	private Authentication authenticate(String username, String password) {
		UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
		if(userDetails==null) {
			throw new BadCredentialsException("Invalid Username!!");
		}
		
		if(!passwordEncoder.matches(password, userDetails.getPassword())) {
			throw new BadCredentialsException("Invalid Password!!");
		}
		return new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
	}

}

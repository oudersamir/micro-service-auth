package com.oudersamir.controllers;

import java.util.Collection;



import java.util.HashSet;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oudersamir.entities.UserEntity;
import com.oudersamir.exception.BusinessResourceException;
import com.oudersamir.requests.UserRequest;
import com.oudersamir.responses.UserResponse;
import com.oudersamir.security.SecurityConstants;
import com.oudersamir.service.UserService;

@RestController
@RequestMapping(SecurityConstants.SIGN_UP_URL+"/*")
public class UserController {
	@Autowired
	private UserService userService;
	
	@GetMapping("/users")
	public ResponseEntity<Collection<UserResponse>> getAllUsers(){
			Collection<UserEntity> users=userService.getAllUser();
			ModelMapper modelMapper=new ModelMapper();
			Collection<UserResponse> usersResponse=new HashSet<>();
			for (UserEntity user : users) {
			UserResponse userResponse=modelMapper.map(user, UserResponse.class);	
			usersResponse.add(userResponse);
			}
	return new ResponseEntity<Collection<UserResponse>>(usersResponse,HttpStatus.FOUND);
}
	
	
	@PostMapping("/register")
	@Transactional
	public ResponseEntity<UserResponse> saveUser(@RequestBody UserRequest userRequest){
		if(!userRequest.getPassword().equals(userRequest.getConfirmPassword()))
			throw new BusinessResourceException("PasswordNotConfirmed", "mote de passe incorrect"
					, HttpStatus.NOT_ACCEPTABLE);	
		
		ModelMapper modelMapper=new ModelMapper();
		UserEntity userEntity=modelMapper.map(userRequest, UserEntity.class);
		UserEntity user=userService.saveOrUpdate(userEntity);
		UserResponse userResponse= modelMapper.map(user,UserResponse.class);	
		return new ResponseEntity<UserResponse>(userResponse,HttpStatus.CREATED);
	}
	
	
	
	@PutMapping("/users")
	public ResponseEntity<UserResponse> updateUser(@RequestBody UserRequest userRequest){
		ModelMapper modelMapper=new ModelMapper();
		UserEntity userEntity=modelMapper.map(userRequest, UserEntity.class);
		UserEntity user=userService.saveOrUpdate(userEntity);
		UserResponse userResponse= modelMapper.map(user,UserResponse.class);	
		return new ResponseEntity<UserResponse>(userResponse,HttpStatus.OK);
	}
	
	
	@DeleteMapping("/users/{userId}")
	public ResponseEntity<Void> deleteUser(@PathVariable(value="userId",required=true) String userId)
	throws BusinessResourceException{
		userService.deleteUser(userId);
		return new ResponseEntity<Void>(HttpStatus.GONE);
	}
	
	@PostMapping("/users/login")
	public ResponseEntity<UserResponse> findUserByUserNameAndPassword(@RequestBody UserRequest userRequest){
	UserEntity userEntity=	userService.findByUserNameAndPassword(userRequest.getUserName(),
			userRequest.getPassword()).get();
	ModelMapper modelMapper= new ModelMapper();
	UserResponse userResponse=modelMapper.map(userEntity, UserResponse.class);
		return new ResponseEntity<UserResponse>(userResponse,HttpStatus.FOUND);
	}
	
	
	@GetMapping("/users/{userId}")
	public ResponseEntity<UserResponse> findByUserId(@PathVariable(value="userId") String userId){
		ModelMapper modelMapper=new ModelMapper();
		UserEntity userEntity=userService.findByUserId(userId).get();
		UserResponse userResponse=modelMapper.map(userEntity, UserResponse.class);
		return new ResponseEntity<UserResponse>(userResponse,HttpStatus.FOUND);
	}
	
	
	
}

package com.bridgelabz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bridgelabz.dto.LoginDto;
import com.bridgelabz.dto.PasswordDto;
import com.bridgelabz.dto.UserDto;
import com.bridgelabz.service.IService;
import com.bridgelabz.utility.ApiResponse;

import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private IService service;

	@PostMapping("/")
	@ApiOperation("This api is used for register new user")
	public Mono<ApiResponse> registerUser(@RequestBody UserDto userDto) {
		return service.registerUser(userDto);
	}
	
	@GetMapping("/{email}")
	@ApiOperation("This api is used for getting all the users")
	public Mono<ApiResponse> getUserByEmail(@PathVariable String email) {
		return service.getUserByEmail(email);
	}
	
	 //    this api is used to update the user
    @PutMapping("/update/{id}")
    @ApiOperation("This api is used for update a user")
    public Mono<ApiResponse> updateUser(@PathVariable String id, @RequestBody UserDto userDto) {
        return service.updateUser(id, userDto);
    }

    //    this api is used to log in
    @PostMapping("/login")
    @ApiOperation("This api is used for user login")
    public Mono<ApiResponse> loginUser(@RequestBody LoginDto loginDto) {
    	return service.userLogin(loginDto);
    }

    //    this api is used to delete the user
    @DeleteMapping("/delete/{id}")
    @ApiOperation("This api is used for delete a user")
    public Mono<ApiResponse> deleteUser(@PathVariable String id) {
    	return service.deleteUser(id);
    }

    //    this api is used to send the password reset link to the user
    @GetMapping("/forgetPassword")
    @ApiOperation("This api is used for sending the password reset link")
    public Mono<ApiResponse> forgetPassword(@RequestParam String email) {
    	return service.forgetPassword(email);
    }

    //    this api is used to change the password
    @PutMapping("/changePassword")
    @ApiOperation("This api is used for reseting the password")
    public Mono<ApiResponse> changePassword(@RequestParam String token, @RequestBody PasswordDto passwordDto) {
    	return service.changePassword(token, passwordDto);
    }

    //    this api is used to verify the user
    @GetMapping("/verifyUser/{token}")
    @ApiOperation("This api is used for verifing the user")
    public Mono<String> verifyUser(@PathVariable String token) {
        return service.verifyUser(token);
    }

}

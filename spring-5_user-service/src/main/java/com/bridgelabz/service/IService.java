package com.bridgelabz.service;

import com.bridgelabz.dto.LoginDto;
import com.bridgelabz.dto.PasswordDto;
import com.bridgelabz.dto.UserDto;
import com.bridgelabz.utility.ApiResponse;

import reactor.core.publisher.Mono;

public interface IService {
	
	public Mono<ApiResponse> getUserByEmail(String email);

	public Mono<ApiResponse> registerUser(UserDto userDto);

	public Mono<ApiResponse> userLogin(LoginDto loginDto);

    public Mono<ApiResponse> updateUser(String id, UserDto userDto);

    public Mono<ApiResponse> deleteUser(String id);

    public Mono<ApiResponse> forgetPassword(String email);

    public Mono<ApiResponse> changePassword(String token, PasswordDto passwordDto);

    public Mono<String> verifyUser(String token);
}

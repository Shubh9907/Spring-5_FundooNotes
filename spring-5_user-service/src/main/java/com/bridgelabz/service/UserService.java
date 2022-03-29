package com.bridgelabz.service;

import java.util.Date;

import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.bridgelabz.configuration.RabbitConfiguration;
import com.bridgelabz.dto.LoginDto;
import com.bridgelabz.dto.PasswordDto;
import com.bridgelabz.dto.UserDto;
import com.bridgelabz.models.User;
import com.bridgelabz.repository.UserRepository;
import com.bridgelabz.utility.ApiResponse;
import com.bridgelabz.utility.JwtToken;
import com.bridgelabz.utility.MailService;
import com.bridgelabz.utility.PasswordEncoder;

import reactor.core.publisher.Mono;

@Service
public class UserService implements IService {
	
	@Autowired
    ModelMapper modelMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtToken jwtToken;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    MailService mailService;

    @Autowired
    Environment environment;
    
    @Autowired
    ApiResponse apiResponse;
    
    @Autowired
    RabbitTemplate template;
    
    ApiResponse userNotFound = new ApiResponse("User Not Found", 401, null);

	@Override
	public Mono<ApiResponse> registerUser(UserDto userDto) {
		return userRepository.findByEmail(userDto.getEmail()).map(user -> {
			return new ApiResponse(environment.getProperty("user.alreadyRegistered"), 1, null);
		}).switchIfEmpty(Mono.defer( () -> {
			User user = modelMapper.map(userDto, User.class);
			user.setRegisterDate(new Date());
	        user.setPassword(encoder.encodePassword(userDto.getPassword()));
	        template.convertAndSend(RabbitConfiguration.EXCHANGE, RabbitConfiguration.ROUTING_KEY1, userDto.getEmail());  
	        userRepository.save(user).subscribe();
	        
	        return Mono.just(new ApiResponse(environment.getProperty("user.successfullyRegistered"), 2, null)); 
		}));
	}

	@Override
	public Mono<ApiResponse> getUserByEmail(String email) {
		return userRepository.findByEmail(email).map(user -> {
			return new ApiResponse("User List", 200, user);
		}).switchIfEmpty(Mono.just(userNotFound));
	}

	@Override
	public Mono<ApiResponse> userLogin(LoginDto loginDto) {
		return userRepository.findByEmail(loginDto.getEmail()).map(user -> {
			BCryptPasswordEncoder pEncoder = new BCryptPasswordEncoder();
			if(!user.getIsVerified()) {
				return new ApiResponse(environment.getProperty("user.verifyEmail"), 1, null);
			}
			if(pEncoder.matches(loginDto.getPassword(), user.getPassword())) {
                String token = jwtToken.generateToken(loginDto.getEmail());
                return new ApiResponse(environment.getProperty("user.loginSuccessfully"), 2, token);
			}else {
				return new ApiResponse(environment.getProperty("user.invalidUser"), 3, null);
			}
		}).switchIfEmpty(Mono.just(userNotFound));
	}

	@Override
	public Mono<ApiResponse> updateUser(String id, UserDto userDto) {
		return userRepository.findById(id).flatMap(user -> {
			if (userDto.getName() != null) {
                user.setName(userDto.getName());
            }
            if (userDto.getEmail() != null) {
                user.setEmail(userDto.getEmail());
            }
            if (userDto.getPassword() != null) {
                user.setPassword(userDto.getPassword());
            }
            if (userDto.getNumber() != null) {
                user.setNumber(userDto.getNumber());
            }
            return userRepository.save(user).map(user1 -> {
            	return new ApiResponse(environment.getProperty("user.update"), 1, null);
            });
		}).switchIfEmpty(Mono.just(userNotFound));
	}

	@Override
	public Mono<ApiResponse> deleteUser(String id) {
		return userRepository.findById(id).flatMap(user -> {
			userRepository.deleteById(id).subscribe();
			return Mono.just(new ApiResponse(environment.getProperty("user.deleted"), 1, null)); 
		}).switchIfEmpty(Mono.just(userNotFound));
	}

	@Override
	public Mono<ApiResponse> forgetPassword(String email) {
		return userRepository.findByEmail(email).map(user -> {
			template.convertAndSend(RabbitConfiguration.EXCHANGE, RabbitConfiguration.ROUTING_KEY2, email);
			return new ApiResponse(environment.getProperty("emailSent"), 1, null);
		}).switchIfEmpty(Mono.just(userNotFound));
	}

	@Override
	public Mono<ApiResponse> changePassword(String token, PasswordDto passwordDto) {
        String email = jwtToken.decodeToken(token);
		return userRepository.findByEmail(email).flatMap(user -> {
			if ((passwordDto.getNewPassword()).matches((passwordDto.getConfirmPassword()))) {
		        String encPass;
				encPass = encoder.encodePassword(passwordDto.getNewPassword());
                user.setPassword(encPass);
                return userRepository.save(user).map(user1 -> {
                	return new ApiResponse(environment.getProperty("user.passwordChanged"), 1, null);
                });
			}else {
				return Mono.just(new ApiResponse(environment.getProperty("differentPassword"), 2, null));
			}
		}).switchIfEmpty(Mono.just(userNotFound));
	}

	@Override
	public Mono<String> verifyUser(String token) {
        String email = jwtToken.decodeToken(token);
		return userRepository.findByEmail(email).map(user -> {
			user.setIsVerified(true);
            userRepository.save(user).subscribe();
            return environment.getProperty("user.verified");
		}).switchIfEmpty(Mono.just(environment.getProperty("error")));
	}
}
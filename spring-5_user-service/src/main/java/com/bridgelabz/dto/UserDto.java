package com.bridgelabz.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserDto {

//	@Pattern(regexp = "$[a-z]{3,}")
    private String name;
    private String email;
    private String password;
    private String number;
}
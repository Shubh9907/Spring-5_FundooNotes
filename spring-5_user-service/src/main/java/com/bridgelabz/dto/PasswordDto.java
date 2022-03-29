package com.bridgelabz.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class PasswordDto {

    private String newPassword;
    private String confirmPassword;
}

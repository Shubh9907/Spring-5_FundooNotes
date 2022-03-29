package com.bridgelabz.utility;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoder {

	public String encodePassword(String password) {
        String encPass = null;
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        encPass = bCryptPasswordEncoder.encode(password);
        return encPass;
    }
}

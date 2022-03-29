package com.bridgelabz.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:message.properties")
public class AppConfiguration {
	
	@Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
